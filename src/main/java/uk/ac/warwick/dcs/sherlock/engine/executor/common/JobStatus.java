package uk.ac.warwick.dcs.sherlock.engine.executor.common;

import org.apache.commons.lang3.time.DurationFormatUtils;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;

public class JobStatus implements Comparable<JobStatus> {

	private static final String[] stdMessages = { "Queued", "Initialising", "Pre-Processing", "Building Workers", "Detecting", "Post-Processing", "Analysing Results", "Finished" , "Failed"};
	private static final float[] stageProgCap = { 0f,        0f,            0.06f,            0.12f,              0.7f,        0.8f,              1.0f,                1.0f,        0f };

	private int id;
	private Instant startTime;
	private Duration duration;
	private Priority priority;

	private final AtomicFloat progress = new AtomicFloat();
	private final AtomicFloat progressIncrement = new AtomicFloat();

	private int step;
	private String message;

	public JobStatus(int id, Priority priority) {
		this.id = id;
		this.startTime = null;
		this.duration = null;

		this.priority = priority;

		this.step = 0;
		this.message = "";
	}

	@Override
	public int compareTo(@NotNull JobStatus o) {
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
		if (this.startTime != null && this.step < 7) {
			this.duration = Duration.between(this.startTime, Instant.now());
			this.setStep(7);
			this.progress.set(1f);
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
		synchronized (this.progress) {
			return progress.get();
		}
	}

	/**
	 * Progress int, percentage complete between 0 and 100
	 *
	 * @return percentage job complete between 0 and 100
	 */
	public int getProgressInt() {
		synchronized (this.progress) {
			return Math.round(progress.get() * 100);
		}
	}

	public void incrementProgress() {
		synchronized (this.progress) {
			this.progress.addTo(this.progressIncrement.get());

			//System.out.println(this.progress.get());

			if (this.progress.get() > 1) {
				this.progress.set(1);
			}
		}
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
		if (step > -1 && step < 8) {
			this.step = step;
		}
	}

	public void calculateProgressIncrement(int nextStepTotalIncrements) {
		synchronized (this.progress) {
			if (nextStepTotalIncrements > 0) {
				this.progressIncrement.set((stageProgCap[this.step] - this.progress.get()) / nextStepTotalIncrements);
			}
			else {
				this.progressIncrement.set(0);
			}
		}
	}

	public void startJob() {
		this.startTime = Instant.now();
		this.step = 1;
	}
}
