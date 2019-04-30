package uk.ac.warwick.dcs.sherlock.engine.executor;

import uk.ac.warwick.dcs.sherlock.api.component.IJob;
import uk.ac.warwick.dcs.sherlock.api.component.WorkStatus;
import uk.ac.warwick.dcs.sherlock.api.executor.IExecutor;
import uk.ac.warwick.dcs.sherlock.api.executor.IJobStatus;
import uk.ac.warwick.dcs.sherlock.engine.SherlockEngine;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.*;
import uk.ac.warwick.dcs.sherlock.engine.executor.pool.PoolExecutorJob;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

/**
 * Basic executor implementation
 */
public class BaseExecutor implements IExecutor, IPriorityWorkSchedulerWrapper {

	final Map<IJob, JobStatus> jobMap;

	private final PriorityBlockingQueue<PoolExecutorJob> queue;
	private PriorityWorkScheduler scheduler;
	private ExecutorService exec;
	private ExecutorService execScheduler;

	private int curID;

	public BaseExecutor() {
		this.scheduler = new PriorityWorkScheduler();

		this.exec = Executors.newSingleThreadExecutor();
		this.execScheduler = Executors.newSingleThreadExecutor();
		this.queue = new PriorityBlockingQueue(5, Comparator.comparing(PoolExecutorJob::getPriority));
		this.jobMap = new HashMap<>();

		this.curID = 0; //counter for jobstatus ids

		this.execScheduler.execute(() -> {
			while (true) {
				try {
					PoolExecutorJob job;
					job = this.queue.take();

					synchronized (ExecutorUtils.logger) {
						ExecutorUtils.logger.info("Job {} starting", job.getId());
					}

					job.getStatus().startJob();

					Future f = this.exec.submit(job);
					f.get();

					job.getStatus().finishJob();

					//Remove after some configured time
					if (job.getJob().getStatus().equals(WorkStatus.COMPLETE) && SherlockEngine.configuration.getJobCompleteDismissalTime() > 0) {
						Thread thread = new Thread(new JobDismisser(this, job));
						thread.start();
					}

					synchronized (ExecutorUtils.logger) {
						ExecutorUtils.logger.info("Job {} finished, took: {}", job.getId(), job.getStatus().getFormattedDuration());
					}
				}
				catch (InterruptedException | ExecutionException e) {
					break;
				}
			}
		});
	}

	@Override
	public List<IJobStatus> getAllJobStatuses() {
		List<IJobStatus> res;
		synchronized (this.jobMap) {
			res = new ArrayList<>(this.jobMap.values());
		}
		res.sort(IJobStatus::compareTo);
		return res;
	}

	@Override
	public IJob getJob(IJobStatus jobStatus) {
		synchronized (this.jobMap) {
			if (this.jobMap.containsValue(jobStatus)) {
				AtomicReference<IJob> ret = new AtomicReference<>(null);
				this.jobMap.forEach((job, status) -> {
					if (jobStatus.equals(status)) {
						ret.set(job);
					}
				});

				return ret.get();
			}
			else {
				return null;
			}
		}
	}

	@Override
	public IJobStatus getJobStatus(IJob job) {
		synchronized (this.jobMap) {
			return this.jobMap.getOrDefault(job, null);
		}
	}

	@Override
	public List<IJob> getWaitingJobs() {
		return this.queue.stream().map(PoolExecutorJob::getJob).collect(Collectors.toList());
	}

	@Override
	public void invokeWork(ForkJoinTask topAction, Priority priority) {
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
	public void shutdown() {
		this.scheduler.shutdown();
		this.exec.shutdownNow();
		this.execScheduler.shutdownNow();
	}

	@Override
	public boolean submitJob(IJob job) {
		if (job == null) {
			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.error("Job is null");
			}
			return false;
		}

		if (!job.isPrepared() || job.getStatus().equals(WorkStatus.NOT_PREPARED)) {
			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.error("Job {} has not been prepared", job.getPersistentId());
			}
			return false;
		}

		if (job.getTasks().isEmpty()) {
			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.error("Job {} does not have any tasks", job.getPersistentId());
			}
			return false;
		}

		if (job.getFiles() == null || job.getFiles().length == 0) {
			synchronized (ExecutorUtils.logger) {
				ExecutorUtils.logger.error("Job {} workspace has no files", job.getPersistentId());
			}
			return false;
		}

		JobStatus s = new JobStatus(curID++, Priority.DEFAULT);
		synchronized (this.jobMap) {
			this.jobMap.put(job, s);
		}

		PoolExecutorJob j = new PoolExecutorJob(this, job, s);
		this.queue.add(j);

		synchronized (ExecutorUtils.logger) {
			ExecutorUtils.logger.info("Job {} added to queue", job.getPersistentId());
		}

		return true;
	}

	@Override
	public boolean dismissJob(IJobStatus jobStatus) {
		return this.dismissJob(this.getJob(jobStatus), jobStatus);
	}

	@Override
	public boolean dismissJob(IJob job) {
		return this.dismissJob(job, this.getJobStatus(job));
	}

	private boolean dismissJob(IJob job, IJobStatus jobStatus) {
		if (this.jobMap.containsKey(job) && jobStatus.isFinished()) {
			synchronized (this.jobMap) {
				this.jobMap.remove(job);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean cancelJob(IJobStatus jobStatus) {
		return false;
	}

	@Override
	public boolean cancelJob(IJob job) {
		return false;
	}

	@Override
	public void submitWork(PriorityWorkTask work) {
		this.scheduler.scheduleJob(work);
	}

	private class JobDismisser implements Runnable {

		private BaseExecutor executor;
		private PoolExecutorJob job;
		private long time;

		JobDismisser(BaseExecutor executor, PoolExecutorJob job) {
			this.executor = executor;
			this.job = job;
			this.time = 60000 * SherlockEngine.configuration.getJobCompleteDismissalTime();
		}

		@Override
		public void run() {
			try {
				Thread.sleep(this.time);
				synchronized (this.executor.jobMap) {
					this.executor.jobMap.remove(this.job.getJob());
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
