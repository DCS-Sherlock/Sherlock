package uk.ac.warwick.dcs.sherlock.engine.executor.priority;

import uk.ac.warwick.dcs.sherlock.engine.component.ITask;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.IPriorityWorkSchedulerWrapper;

public class PoolExecutorTask implements Runnable {

	private IPriorityWorkSchedulerWrapper scheduler;
	private ITask task;

	PoolExecutorTask(IPriorityWorkSchedulerWrapper scheduler, ITask task) {
		this.scheduler = scheduler;
		this.task = task;
	}

	@Override
	public void run() {

	}
}

