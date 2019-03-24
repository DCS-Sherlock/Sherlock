package uk.ac.warwick.dcs.sherlock.engine.executor;

import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.component.WorkStatus;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.*;
import uk.ac.warwick.dcs.sherlock.engine.executor.pool.PoolExecutorJob;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class PoolExecutor implements IExecutor, IPriorityWorkSchedulerWrapper {

	private final PriorityBlockingQueue<PoolExecutorJob> queue;
	private final Map<IJob, JobStatus> jobMap;
	private PriorityWorkScheduler scheduler;
	private ExecutorService exec;
	private ExecutorService execScheduler;

	private int curID;

	public PoolExecutor() {
		this.scheduler = new PriorityWorkScheduler();

		this.exec = Executors.newSingleThreadExecutor();
		this.execScheduler = Executors.newSingleThreadExecutor();
		this.queue = new PriorityBlockingQueue(5, Comparator.comparing(PoolExecutorJob::getPriority));
		this.jobMap = new HashMap<>();

		this.curID = 0;

		this.execScheduler.execute(() -> {
			while (true) {
				try {
					PoolExecutorJob job;
					job = this.queue.take();

					synchronized (ExecutorUtils.logger) {
						ExecutorUtils.logger.info("Job {} starting", job.getId());
					}

					this.getAllJobStatuses().forEach(x -> System.out.println(x.getMessage()));

					job.getStatus().startJob();

					Future f = this.exec.submit(job);
					f.get();

					job.getStatus().finishJob();

					//Remove after a minute
					Runnable runnable = () -> {
						try {
							Thread.sleep(60000);
							synchronized (this.jobMap) {
								this.jobMap.remove(job.getJob());
							}
						}
						catch (InterruptedException e) {
							e.printStackTrace();
						}
					};
					Thread thread = new Thread(runnable);
					thread.start();

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
	public List<JobStatus> getAllJobStatuses() {
		List<JobStatus> res;
		synchronized (this.jobMap) {
			 res = new ArrayList<>(this.jobMap.values());
		}
		res.sort(JobStatus::compareTo);
		return res;
	}

	@Override
	public List<IJob> getWaitingJobs() {
		return this.queue.stream().map(PoolExecutorJob::getJob).collect(Collectors.toList());
	}

	@Override
	public JobStatus getJobStatus(IJob job) {
		synchronized (this.jobMap) {
			return this.jobMap.getOrDefault(job, null);
		}
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
	public void submitWork(PriorityWorkTask work) {
		this.scheduler.scheduleJob(work);
	}
}
