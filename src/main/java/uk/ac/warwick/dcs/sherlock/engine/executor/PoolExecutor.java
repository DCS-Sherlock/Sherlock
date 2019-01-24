package uk.ac.warwick.dcs.sherlock.engine.executor;

import uk.ac.warwick.dcs.sherlock.engine.component.IJob;
import uk.ac.warwick.dcs.sherlock.engine.component.WorkStatus;
import uk.ac.warwick.dcs.sherlock.engine.executor.common.*;
import uk.ac.warwick.dcs.sherlock.engine.executor.pool.PoolExecutorJob;

import java.util.*;
import java.util.concurrent.*;

public class PoolExecutor implements IExecutor, IPriorityWorkSchedulerWrapper {

	private final PriorityBlockingQueue<PoolExecutorJob> queue;
	private final Map<IJob, JobStatus> jobMap;
	private PriorityWorkScheduler scheduler;
	private ExecutorService exec;
	private ExecutorService execScheduler;

	public PoolExecutor() {
		this.scheduler = new PriorityWorkScheduler();

		this.exec = Executors.newSingleThreadExecutor();
		this.execScheduler = Executors.newSingleThreadExecutor();
		this.queue = new PriorityBlockingQueue(5, Comparator.comparing(PoolExecutorJob::getPriority));
		this.jobMap = new HashMap<>();

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

					synchronized (ExecutorUtils.logger) {
						ExecutorUtils.logger.info("Job {} finished, took: {}", job.getId(), job.getStatus().formatDuration());
					}
				}
				catch (InterruptedException | ExecutionException e) {
					break;
				}
			}
		});
	}

	@Override
	public List<IJob> getCurrentJobs() {
		return null;
	}

	@Override
	public JobStatus getJobStatus(IJob job) {
		return null;
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

		/*List<ISourceFile> f = job.getWorkspace().getFiles();
		IResultJob res = job.createNewResult();
		res.addFile(f.get(0));
		res.addFile(f.get(1));*/

		JobStatus s = new JobStatus(Priority.DEFAULT);

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
