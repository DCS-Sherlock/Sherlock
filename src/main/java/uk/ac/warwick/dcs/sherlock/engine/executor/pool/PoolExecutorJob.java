package uk.ac.warwick.dcs.sherlock.engine.executor.pool;

import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.ExecutorUtils;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.IPriorityWorkSchedulerWrapper;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.JobStatus;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.Priority;
import uk.ac.warwick.dcs.sherlock.engine.executor.work.WorkPreProcessFiles;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class PoolExecutorJob implements Runnable{

	private IPriorityWorkSchedulerWrapper scheduler;
	private IJob job;
	private JobStatus status;

	public PoolExecutorJob(IPriorityWorkSchedulerWrapper scheduler, IJob job, JobStatus status) {
		this.scheduler = scheduler;
		this.job = job;
		this.status = status;
	}

	public JobStatus getStatus() {
		return status;
	}

	public Priority getPriority() {
		return this.status.getPriority();
	}

	@Override
	public void run() {
		List<PoolExecutorTask> tasks = job.getTasks().stream().map(x -> new PoolExecutorTask(scheduler, x, job.getWorkspace().getLanguage())).collect(Collectors.toList());

		RecursiveAction preProcess = new WorkPreProcessFiles(new ArrayList<>(tasks), this.job.getWorkspace().getFiles());
		this.scheduler.invokeWork(preProcess, Priority.DEFAULT);

		// Check that preprocessing went okay
		tasks.stream().filter(x -> x.dataItems.size() == 0).peek(x -> {
			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.error("PreProcessing output for detector {} is empty, this detector will be ignored.", x.getDetector().getName());
			}
		}).forEach(tasks::remove);

		ExecutorService exServ = Executors.newFixedThreadPool(tasks.size());
		try {
			exServ.invokeAll(new LinkedList<>(tasks));
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		/*synchronized (ExecutorUtils.logger) {
			ExecutorUtils.logger.info("Done!!");
		}*/
	}
}
