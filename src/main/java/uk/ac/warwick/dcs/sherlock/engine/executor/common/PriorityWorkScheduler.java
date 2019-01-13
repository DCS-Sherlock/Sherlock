package uk.ac.warwick.dcs.sherlock.engine.executor.common;

import java.util.*;
import java.util.concurrent.*;

public class PriorityWorkScheduler {

	private ForkJoinPool priorityWorkForkPool;
	private ExecutorService priorityWorkScheduler;

	private PriorityBlockingQueue<PriorityWorkTask> priorityQueue;

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

					System.out.println("syncing work");
					synchronized (nextTask) {
						System.out.println("entered work");
						Thread.sleep(3000);
						System.out.println("starting work");

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
