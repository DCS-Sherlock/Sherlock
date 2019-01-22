package uk.ac.warwick.dcs.sherlock.engine.executor.pool;

import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.component.WorkStatus;
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

			List<PoolExecutorTask> detTasks = tasks.stream().filter(x -> x.getStatus() != WorkStatus.COMPLETE).collect(Collectors.toList());

			RecursiveAction preProcess = new WorkPreProcessFiles(new ArrayList<>(detTasks), this.job.getWorkspace().getFiles());
			this.scheduler.invokeWork(preProcess, Priority.DEFAULT);

			// Check that preprocessing went okay
			detTasks.stream().filter(x -> x.dataItems.size() == 0).peek(x -> {
				synchronized (ExecutorUtils.logger) {
					ExecutorUtils.logger.error("PreProcessing output for detector {} is empty, this detector will be ignored.", x.getDetector().getName());
				}
			}).forEach(detTasks::remove);

			try {
				exServ.invokeAll(detTasks);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			job.setStatus(WorkStatus.REGEN_RESULTS);
		}

		// do the post stuff
		tasks.forEach( x -> x.callType = 2);
		List<PoolExecutorTask> postTasks = tasks.stream().filter(x -> x.getStatus() == WorkStatus.COMPLETE).collect(Collectors.toList());
		try {
			exServ.invokeAll(postTasks);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		job.setStatus(WorkStatus.COMPLETE);
	}
}
