package uk.ac.warwick.dcs.sherlock.engine.executor.common;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.time.Instant;

public class JobStatus {

	private Instant startTime;
	private Duration duration;

	private Priority priority;

	private float progress;
	private String message;

	public JobStatus(Priority priority) {
		this.startTime = null;
		this.duration = null;

		this.priority = priority;

		this.progress = 0;
		this.message = "Initialisation";
	}

	public void finishJob() {
		if (this.startTime != null) {
			this.duration = Duration.between(this.startTime, Instant.now());
			this.message = "Finished";
			this.startTime = null;
		}
	}

	public Duration getDuration() {
		return this.duration;
	}

	public String formatDuration() {
		return DurationFormatUtils.formatDuration(this.duration.toMillis(), "H:mm:ss.SSSS", true);
	}

	public Priority getPriority() {
		return priority;
	}

	public boolean isFinished() {
		return duration != null;
	}

	public void startJob() {
		this.startTime = Instant.now();
		this.message = "Starting";
	}

}
