package uk.ac.warwick.dcs.sherlock.engine.executor.pool;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlock;
import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
import uk.ac.warwick.dcs.sherlock.api.exception.UnknownDetectionTypeException;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.ModelTaskProcessedResults;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;
import uk.ac.warwick.dcs.sherlock.api.util.Tuple;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.component.*;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.ExecutorUtils;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.IPriorityWorkSchedulerWrapper;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.JobStatus;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.Priority;
import uk.ac.warwick.dcs.sherlock.engine.executor.work.WorkPreProcessFiles;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

public class PoolExecutorJob implements Runnable {

	private IPriorityWorkSchedulerWrapper scheduler;
	private IJob job;
	private JobStatus status;

	public PoolExecutorJob(IPriorityWorkSchedulerWrapper scheduler, IJob job, JobStatus status) {
		this.scheduler = scheduler;
		this.job = job;
		this.status = status;
	}

	public long getId() {
		return this.job.getPersistentId();
	}

	public Priority getPriority() {
		return this.status.getPriority();
	}

	public JobStatus getStatus() {
		return this.status;
	}

	public IJob getJob() {
		return this.job;
	}

	@Override
	public void run() {
		List<PoolExecutorTask> tasks = job.getTasks().stream().map(x -> new PoolExecutorTask(this.status, scheduler, x, job.getWorkspace().getLanguage())).collect(Collectors.toList());
		ExecutorService exServ = Executors.newFixedThreadPool(tasks.size());

		if (!(job.getStatus() == WorkStatus.COMPLETE || job.getStatus() == WorkStatus.REGEN_RESULTS) && tasks.size() > 0) {
			job.setStatus(WorkStatus.ACTIVE);
			this.status.nextStep();
			this.status.calculateProgressIncrement(tasks.stream().mapToInt(t -> t.getPreProcessingStrategies().size()).sum() * this.job.getWorkspace().getFiles().size());

			List<PoolExecutorTask> detTasks = tasks.stream().filter(x -> x.getStatus() != WorkStatus.COMPLETE).collect(Collectors.toList());

			RecursiveAction preProcess = new WorkPreProcessFiles(new ArrayList<>(detTasks), this.job.getWorkspace().getFiles());
			this.scheduler.invokeWork(preProcess, Priority.DEFAULT);

			// Check that preprocessing went okay
			detTasks.stream().filter(x -> x.dataItems.size() == 0).peek(x -> {
				synchronized (ExecutorUtils.logger) {
					ExecutorUtils.logger.error("PreProcessing output for detector {} is empty, this detector will be ignored.", x.getDetector().getName());
				}
			}).forEach(detTasks::remove);

			this.status.nextStep();
			this.status.calculateProgressIncrement(detTasks.size());
			try {
				exServ.invokeAll(detTasks);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}

			this.status.nextStep();
			this.status.calculateProgressIncrement(detTasks.stream().mapToInt(PoolExecutorTask::getWorkerSize).sum());
			try {
				exServ.invokeAll(detTasks);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}

			job.setStatus(WorkStatus.REGEN_RESULTS);
		}
		else {
			job.setStatus(WorkStatus.ACTIVE);
			tasks.forEach(x -> x.callType = 3);
		}

		// do the post stuff
		this.status.setStep(5);
		List<PoolExecutorTask> postTasks = tasks.stream().filter(x -> x.getStatus() == WorkStatus.COMPLETE).collect(Collectors.toList());
		List<ITuple<ITask, ModelTaskProcessedResults>> results = new LinkedList<>();
		this.status.calculateProgressIncrement(postTasks.size());

		try {
			List<Future<ModelTaskProcessedResults>> tmp = exServ.invokeAll(postTasks);
			for (int i = 0; i < postTasks.size(); i++) {
				ModelTaskProcessedResults m = tmp.get(i).get();
				if (m != null && m.getGroups().size() > 0) {
					results.add(new Tuple<>(postTasks.get(i).getTask(), m));
				}
			}
		}
		catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		if (results.size() > 0) {
			this.status.nextStep();
			this.status.calculateProgressIncrement(((this.job.getWorkspace().getFiles().size() * results.size()) * 2) + this.job.getWorkspace().getFiles().size());

			List<ICodeBlockGroup> allGroups = results.stream().flatMap(f -> f.getValue().getGroups().stream()).collect(Collectors.toList());
			SherlockEngine.storage.storeCodeBlockGroups(allGroups);

			// TODO: thread scoring loops
			IResultJob jobRes = this.job.createNewResult();
			for (ISourceFile file : this.job.getWorkspace().getFiles()) {
				IResultFile fileRes = jobRes.addFile(file);
				List<ITuple<ICodeBlockGroup, Float>> overallGroupScores = new LinkedList<>();

				for (ITuple<ITask, ModelTaskProcessedResults> t : results) {
					try {
						List<ICodeBlockGroup> groupsContainingFile = t.getValue().getGroups(file);
						int fileTotal = t.getValue().getFileTotal(file);

						// Construct block scores weighted against the whole file, by default uses file line count, but can be set to custom totals (eg. variable counts)
						AtomicReference<Float> fullSize = new AtomicReference<>((float) 0);
						List<ITuple<ICodeBlockGroup, Float>> groupScores = groupsContainingFile.stream().map(x -> {
							ICodeBlock b = x.getCodeBlock(file);
							float size = b.getLineNumbers().stream().mapToInt(y -> y.getValue()- y.getKey() + 1).sum();
							fullSize.updateAndGet(v -> v + size);
							return new Tuple<>(x, b.getBlockScore() * (size/fileTotal));
						}).collect(Collectors.toList());

						// Normalise against full size to counteract overlaps
						if (fullSize.get() > fileTotal) {
							float factor = fullSize.get() / fileTotal;
							groupScores.forEach(x -> x.setValue(x.getValue()/factor));
						}
						this.status.incrementProgress();

						IResultTask taskRes = fileRes.addTaskResult(t.getKey());
						taskRes.addContainingBlock(groupsContainingFile);

						// calculate and store the scores from the group scores, uses weightings
						calculateScoreForBlockList(file, groupScores, taskRes, taskRes.getClass().getDeclaredMethod("setTaskScore", float.class), taskRes.getClass().getDeclaredMethod("addFileScore", ISourceFile.class, float.class));
						overallGroupScores.addAll(groupScores);

						this.status.incrementProgress();
					}
					catch (Exception e) {
						synchronized (ExecutorUtils.logger) {
							ExecutorUtils.logger.error("Scorer error: ", e);
						}
					}
				}

				try {
					calculateScoreForBlockList(file, overallGroupScores, fileRes, fileRes.getClass().getDeclaredMethod("setOverallScore", float.class), fileRes.getClass().getDeclaredMethod("addFileScore", ISourceFile.class, float.class));
				}
				catch (NoSuchMethodException e) {
					e.printStackTrace();
				}

				this.status.incrementProgress();
			}
		}
		else {
			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.info("Job {} produced no results", job.getPersistentId());
			}
		}

		job.setStatus(WorkStatus.COMPLETE);
	}

	@SuppressWarnings ("Duplicates")
	private void calculateScoreForBlockList(ISourceFile file, List<ITuple<ICodeBlockGroup, Float>> groupScores, Object obj, Method methodTotal, Method methodPerFile) {
		try {
			// Calculate types and their relative weightings within this task
			Map<DetectionType, Double> typeWeights = new HashMap<>();
			for (ITuple<ICodeBlockGroup, Float> g : groupScores) {
				typeWeights.putIfAbsent(g.getKey().getDetectionType(), 0.0);
			}

			double weightSum = typeWeights.keySet().stream().mapToDouble(DetectionType::getWeighting).sum();
			typeWeights.keySet().forEach(z -> typeWeights.put(z, z.getWeighting() / weightSum));

			// Score the task overall from relative weightings
			float s = (float) groupScores.stream().mapToDouble(x -> {
				try {
					return x.getValue() * typeWeights.get(x.getKey().getDetectionType());
				}
				catch (UnknownDetectionTypeException e) {
					e.printStackTrace();
					return 0;
				}
			}).sum();
			methodTotal.invoke(obj, /*s > 1 ? 1 : s*/ s); // cap to 100%

			// Score each file for the task from relative weightings
			for (ISourceFile fileComp : this.job.getWorkspace().getFiles()) {
				if (!fileComp.equals(file)) {
					s = (float) groupScores.stream().filter(g -> g.getKey().filePresent(fileComp)).mapToDouble(x -> {
						try {
							return x.getValue() * typeWeights.get(x.getKey().getDetectionType());
						}
						catch (UnknownDetectionTypeException e) {
							e.printStackTrace();
							return 0;
						}
					}).sum();
					methodPerFile.invoke(obj, fileComp, /*s > 1 ? 1 : s*/ s);  // cap to 100%
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
