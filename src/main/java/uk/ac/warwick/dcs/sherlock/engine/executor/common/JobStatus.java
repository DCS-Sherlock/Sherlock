package uk.ac.warwick.dcs.sherlock.engine.executor.common;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.time.Instant;

public class JobStatus implements Comparable<JobStatus> {

	private static final String[] stdMessages = { "Queued", "Initialising", "Pre-Processing", "Detecting", "Post-Processing", "Finalising Results", "Finished" };
	private int id;
	private Instant startTime;
	private Duration duration;
	private Priority priority;
	private float progress;
	private int step;
	private String message;

	public JobStatus(int id, Priority priority) {
		this.id = id;
		this.startTime = null;
		this.duration = null;

		this.priority = priority;

		this.progress = 0;
		this.step = 0;
		this.message = "";
	}

	@Override
	public int compareTo(JobStatus o) {
		int r = Integer.compare(o.step, this.step);
		if (r == 0) {
			if (this.startTime != null && o.startTime != null) {
				return o.startTime.compareTo(this.startTime);
			}
			else if (this.startTime != null) {
				return -1;
			}
			else if (o.startTime != null) {
				return 1;
			}
			else {
				return 0;
			}
		}
		else {
			return r;
		}
	}

	public void finishJob() {
		if (this.startTime != null) {
			this.duration = Duration.between(this.startTime, Instant.now());
			this.step = 6;
			this.startTime = null;
		}
	}

	public Duration getDuration() {
		return this.isFinished() ? this.duration : Duration.between(this.startTime, Instant.now());
	}

	public String getFormattedDuration() {
		return DurationFormatUtils.formatDuration(this.getDuration().toMillis(), "H:mm:ss.SSSS", true);
	}

	public int getId() {
		return id;
	}

	public String getMessage() {
		return this.message.equals("") ? stdMessages[this.step] : this.message;
	}

	public void setMessage(String msg) {
		this.message = msg;
	}

	/**
	 * Internal priority for the job in the run queue, probably don't show to the user.
	 *
	 * @return the job priority
	 */
	public Priority getPriority() {
		return priority;
	}

	/**
	 * Progress float, percentage complete between 0 and 1
	 *
	 * @return percentage job complete between 0 and 1
	 */
	public float getProgress() {
		return progress;
	}

	public boolean isFinished() {
		return duration != null;
	}

	public void nextStep() {
		if (this.step < 6) {
			this.step++;
		}
	}

	public void setStep(int step) {
		if (step > -1 && step < 7) {
			this.step = step;
		}
	}

	public void startJob() {
		this.startTime = Instant.now();
		this.step = 1;
	}

}
