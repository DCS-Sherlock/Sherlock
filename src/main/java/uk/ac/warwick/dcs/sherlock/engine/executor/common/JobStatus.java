package uk.ac.warwick.dcs.sherlock.engine.executor.common;

import java.time.Duration;

public class JobStatus {

	private long startTime;
	private Duration duration;

	private Priority priority;

	private float progress;
	private String message;

	public JobStatus(Priority priority) {
		this.startTime = 0;
		this.duration = null;

		this.priority = priority;

		this.progress = 0;
		this.message = "Initialisation";
	}

	public void finishJob() {
		this.duration = Duration.ofNanos(Math.round(System.nanoTime() - this.startTime));
		this.message = "Finished";
	}

	public Duration getDuration() {
		return this.duration;
	}

	public Priority getPriority() {
		return priority;
	}

	public boolean isFinished() {
		return duration != null;
	}

	public void startJob() {
		this.startTime = System.nanoTime();
		this.message = "Starting";
	}

}
