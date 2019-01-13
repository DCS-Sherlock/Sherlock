package uk.ac.warwick.dcs.sherlock.engine.executor.priority;

import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.IPriorityWorkSchedulerWrapper;

import java.util.*;
import java.util.stream.*;

public class PoolExecutorJob implements Runnable{

	private IPriorityWorkSchedulerWrapper scheduler;

	private IJob job;
	private List<PoolExecutorTask> tasks;

	public PoolExecutorJob(IPriorityWorkSchedulerWrapper scheduler, IJob job) {
		this.scheduler = scheduler;
		this.job = job;
		this.tasks = job.getTasks().stream().map(x -> new PoolExecutorTask(scheduler, x)).collect(Collectors.toList());
	}

	@Override
	public void run() {

	}
}
