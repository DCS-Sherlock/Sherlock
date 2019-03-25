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

import java.util.*;
import java.util.concurrent.*;
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

	@SuppressWarnings ("Duplicates")
	@Override
	public void run() {
		List<PoolExecutorTask> tasks = job.getTasks().stream().map(x -> new PoolExecutorTask(scheduler, x, job.getWorkspace().getLanguage())).collect(Collectors.toList());
		ExecutorService exServ = Executors.newFixedThreadPool(tasks.size());

		if (!(job.getStatus() == WorkStatus.COMPLETE || job.getStatus() == WorkStatus.REGEN_RESULTS) && tasks.size() > 0) {
			job.setStatus(WorkStatus.ACTIVE);
			this.status.nextStep();

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
			try {
				exServ.invokeAll(detTasks);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			job.setStatus(WorkStatus.REGEN_RESULTS);
		}
		this.status.setStep(4);

		// do the post stuff
		tasks.forEach(x -> x.callType = 2);
		List<PoolExecutorTask> postTasks = tasks.stream().filter(x -> x.getStatus() == WorkStatus.COMPLETE).collect(Collectors.toList());
		List<ITuple<ITask, ModelTaskProcessedResults>> results = new LinkedList<>();

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

			List<ICodeBlockGroup> allGroups = results.stream().flatMap(f -> f.getValue().getGroups().stream()).collect(Collectors.toList());
			SherlockEngine.storage.storeCodeBlockGroups(allGroups);

			// TODO: thread scoring loops
			float s;
			IResultJob jobRes = this.job.createNewResult();
			for (ISourceFile file : this.job.getWorkspace().getFiles()) {
				IResultFile fileRes = jobRes.addFile(file);

				for (ITuple<ITask, ModelTaskProcessedResults> t : results) {
					try {
						List<ICodeBlockGroup> groupsContainingFile = t.getValue().getGroups(file);
						int fileTotal = t.getValue().getFileTotal(file);

						// Construct block scores weighted against the whole file, by default uses file line count, but can be set to custom totals (eg. variable counts)
						List<ITuple<ICodeBlockGroup, Float>> groupScores = groupsContainingFile.stream().map(x -> {
							ICodeBlock b = x.getCodeBlock(file);
							float size = b.getLineNumbers().stream().mapToInt(y -> y.getValue()- y.getKey() + 1).sum();
							return new Tuple<>(x, b.getBlockScore() * (size/fileTotal));
						}).collect(Collectors.toList());

						IResultTask taskRes = fileRes.addTaskResult(t.getKey());
						taskRes.addContainingBlock(groupsContainingFile);

						// Calculate types and their relative weightings within this task
						Map<DetectionType, Double> typeWeights = new HashMap<>();
						for (ICodeBlockGroup g : groupsContainingFile) {
							typeWeights.putIfAbsent(g.getDetectionType(), 0.0);
						}

						double weightSum = typeWeights.keySet().stream().mapToDouble(DetectionType::getWeighting).sum();
						typeWeights.keySet().forEach(z -> typeWeights.put(z, z.getWeighting()/weightSum));

						// Score the task overall from relative weightings
						s = (float) groupScores.stream().mapToDouble(x -> {
							try {
								return x.getValue() * typeWeights.get(x.getKey().getDetectionType());
							}
							catch (UnknownDetectionTypeException e) {
								e.printStackTrace();
								return 0;
							}
						}).sum();
						taskRes.setTaskScore(/*s > 1 ? 1 : s*/ s); // cap to 100%

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
								taskRes.addFileScore(fileComp, /*s > 1 ? 1 : s*/ s);  // cap to 100%
							}
						}
					}
					catch (Exception e) {
						synchronized (ExecutorUtils.logger) {
							ExecutorUtils.logger.error("Scorer error: ", e);
						}
					}
				}

				// DO SCORING
				for (ISourceFile fileComp : this.job.getWorkspace().getFiles()) {
					if (!fileComp.equals(file)) {
						s = (float) fileRes.getTaskResults().stream().mapToDouble(t -> t.getFileScore(fileComp)).average().orElse(0);
						fileRes.addFileScore(fileComp, s);
					}
				}
				fileRes.setOverallScore(ExecutorUtils.aggregateScores(fileRes.getFileScores().values()));
			}
		}
		else {
			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.info("Job {} produced no results", job.getPersistentId());
			}
		}

		job.setStatus(WorkStatus.COMPLETE);
	}
}
