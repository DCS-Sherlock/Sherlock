package uk.ac.warwick.dcs.sherlock.engine.executor.pool;

import uk.ac.warwick.dcs.sherlock.api.common.ICodeBlockGroup;
import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;
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
		return status;
	}

	@Override
	public void run() {
		List<PoolExecutorTask> tasks = job.getTasks().stream().map(x -> new PoolExecutorTask(scheduler, x, job.getWorkspace().getLanguage())).collect(Collectors.toList());
		ExecutorService exServ = Executors.newFixedThreadPool(tasks.size());

		if (!(job.getStatus() == WorkStatus.COMPLETE || job.getStatus() == WorkStatus.REGEN_RESULTS) && tasks.size() > 0) {
			job.setStatus(WorkStatus.ACTIVE);

			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.info("Job {} preprocessing", job.getPersistentId());
			}

			List<PoolExecutorTask> detTasks = tasks.stream().filter(x -> x.getStatus() != WorkStatus.COMPLETE).collect(Collectors.toList());

			RecursiveAction preProcess = new WorkPreProcessFiles(new ArrayList<>(detTasks), this.job.getWorkspace().getFiles());
			this.scheduler.invokeWork(preProcess, Priority.DEFAULT);

			// Check that preprocessing went okay
			detTasks.stream().filter(x -> x.dataItems.size() == 0).peek(x -> {
				synchronized (ExecutorUtils.logger) {
					ExecutorUtils.logger.error("PreProcessing output for detector {} is empty, this detector will be ignored.", x.getDetector().getName());
				}
			}).forEach(detTasks::remove);

			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.info("Job {} detecting", job.getPersistentId());
			}

			try {
				exServ.invokeAll(detTasks);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			job.setStatus(WorkStatus.REGEN_RESULTS);
		}

		synchronized (ExecutorUtils.logger) {
			ExecutorUtils.logger.info("Job {} postprocessing", job.getPersistentId());
		}

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
			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.info("Job {} finalising", job.getPersistentId());
			}

			List<ICodeBlockGroup> allGroups = results.stream().flatMap(f -> f.getValue().getGroups().stream()).collect(Collectors.toList());
			SherlockEngine.storage.storeCodeBlockGroups(allGroups);

			// TODO: thread scoring loops
			float s;
			IResultJob jobRes = this.job.createNewResult();
			for (ISourceFile file : this.job.getWorkspace().getFiles()) {
				IResultFile fileRes = jobRes.addFile(file);
				for (ITuple<ITask, ModelTaskProcessedResults> t : results) {
					IResultTask taskRes = fileRes.addTaskResult(t.getKey());
					List<ICodeBlockGroup> groupsContainingFile = t.getValue().getGroups(file);
					taskRes.addContainingBlock(groupsContainingFile);

					//DO SCORING
					try {
						for (ISourceFile fileComp : this.job.getWorkspace().getFiles()) {
							if (!fileComp.equals(file)) {
								s = t.getValue().getScorer().score(file, fileComp, groupsContainingFile.stream().filter(g -> g.filePresent(fileComp)).collect(Collectors.toList()));
								taskRes.addFileScore(fileComp, s);
							}
						}
						taskRes.setTaskScore(ExecutorUtils.aggregateScores(taskRes.getFileScores().values()));
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

			jobRes.store();
		}
		else {
			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.info("Job {} produced no results", job.getPersistentId());
			}
		}

		job.setStatus(WorkStatus.COMPLETE);
	}
}
