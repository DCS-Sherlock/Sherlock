package uk.ac.warwick.dcs.sherlock.engine.executor.pool;

import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.ExecutorLogger;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.IPriorityWorkSchedulerWrapper;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.PriorityWorkPriorities;
import uk.ac.warwick.dcs.sherlock.engine.executor.work.WorkPreProcessFiles;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class PoolExecutorJob implements Runnable{

	private IPriorityWorkSchedulerWrapper scheduler;

	private IJob job;
	private List<PoolExecutorTask> tasks;

	public PoolExecutorJob(IPriorityWorkSchedulerWrapper scheduler, IJob job) {
		this.scheduler = scheduler;
		this.job = job;
	}

	@Override
	public void run() {
		this.tasks = job.getTasks().stream().map(x -> new PoolExecutorTask(scheduler, x, job.getWorkspace().getLanguage())).collect(Collectors.toList());

		RecursiveAction preProcess = new WorkPreProcessFiles(new LinkedList<>(this.tasks), this.job.getWorkspace().getFiles());
		this.scheduler.invokeWork(preProcess, PriorityWorkPriorities.DEFAULT);

		// Check that preprocessing went okay
		this.tasks.stream().filter(x -> x.dataItems.size() == 0).peek(x -> {
			synchronized (ExecutorLogger.logger) {
				ExecutorLogger.logger.error("PreProcessing output for detector {} is empty, this detector will be ignored.", x.getDetector().getName());
			}
		}).forEach(this.tasks::remove);


	}
}
