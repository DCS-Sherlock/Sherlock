package uk.ac.warwick.dcs.sherlock.engine.executor;

import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.*;
import uk.ac.warwick.dcs.sherlock.engine.executor.pool.PoolExecutorJob;

import java.util.*;
import java.util.concurrent.*;

public class PoolExecutor implements IExecutor, IPriorityWorkSchedulerWrapper {

	private ExecutorService exec;
	private PriorityWorkScheduler scheduler;

	public PoolExecutor() {
		this.exec = Executors.newSingleThreadExecutor();
		this.scheduler = new PriorityWorkScheduler();
	}

	@Override
	public void invokeWork(ForkJoinTask topAction, PriorityWorkPriorities priority) {
		PriorityWorkTask task = new PriorityWorkTask(topAction, priority);

		synchronized (task) {
			this.submitWork(task);

			try {
				task.wait();
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void submitWork(PriorityWorkTask work) {
		this.scheduler.scheduleJob(work);
	}

	@Override
	public int submitJob(IJob job) {
		//do checks on the job, check it has tasks etc

		PoolExecutorJob j = new PoolExecutorJob(this, job);
		j.run();
		//j.exec.execute(j);


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
