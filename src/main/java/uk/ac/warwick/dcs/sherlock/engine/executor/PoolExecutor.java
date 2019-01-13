package uk.ac.warwick.dcs.sherlock.engine.executor;

import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.*;

import java.util.*;
import java.util.concurrent.*;

public class PoolExecutor implements IExecutor, IPriorityWorkSchedulerWrapper {

	private PriorityWorkScheduler scheduler;

	public PoolExecutor() {
		this.scheduler = new PriorityWorkScheduler();
	}

	@Override
	public void invokeWork(RecursiveAction topAction, PriorityWorkPriorities priority) {
		PriorityWorkTask task = new PriorityWorkTask(topAction, priority);

		synchronized (task) {
			this.submitWork(task);

			try {
				task.wait();
				Thread.sleep(5000);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Work done");
		}
	}

	@Override
	public void submitWork(PriorityWorkTask work) {
		this.scheduler.scheduleJob(work);
	}

	@Override
	public int submitJob(IJob job) {
		//do checks on the job, check it has tasks etc
		return 0;
	}

	@Override
	public List<IJob> getCurrentActiveJobs() {
		return null;
	}

	@Override
	public JobStatus getJobStatus(IJob job) {
		return null;
	}

	@Override
	public void shutdown() {
		this.scheduler.shutdown();
	}
}
