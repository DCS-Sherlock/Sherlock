package uk.ac.warwick.dcs.sherlock.engine.executor.common;

import java.util.*;
import java.util.concurrent.*;

/**
 * Scheduler which does work in priority order
 */
public class PriorityWorkScheduler {

	private final PriorityBlockingQueue<PriorityWorkTask> priorityQueue;
	private ForkJoinPool priorityWorkForkPool;
	private ExecutorService priorityWorkScheduler;

	public PriorityWorkScheduler() {
		this(10);
	}

	public PriorityWorkScheduler(Integer queueSize) {
		this.priorityWorkForkPool = ForkJoinPool.commonPool();
		this.priorityWorkScheduler = Executors.newSingleThreadExecutor();

		this.priorityQueue = new PriorityBlockingQueue<>(queueSize, Comparator.comparing(PriorityWorkTask::getPriority));

		this.priorityWorkScheduler.execute(() -> {
			while (true) {
				try {
					PriorityWorkTask nextTask = this.priorityQueue.take();

					synchronized (nextTask) {
						this.priorityWorkForkPool.execute(nextTask.getTopAction());
						nextTask.getTopAction().join();
						nextTask.notifyAll();
					}

				}
				catch (InterruptedException e) {
					break;
				}
			}
		});
	}

	public void scheduleJob(PriorityWorkTask work) {
		priorityQueue.add(work);
	}

	public void shutdown() {
		this.priorityWorkForkPool.shutdown();
		this.priorityWorkScheduler.shutdownNow();
	}

}
