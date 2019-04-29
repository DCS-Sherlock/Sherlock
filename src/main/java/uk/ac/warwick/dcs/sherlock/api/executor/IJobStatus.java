package uk.ac.warwick.dcs.sherlock.api.executor;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.Instant;

/**
 * Interface defining the status information available for a job
 */
public interface IJobStatus extends Comparable<IJobStatus> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	int compareTo(@NotNull IJobStatus o);

	/**
	 * fetches the current duration of the job, if job finished gives the total runtime
	 *
	 * @return duration
	 */
	Duration getDuration();

	/**
	 * Fetches the current duration of the job as a formatted string, if job finished gives the total runtime
	 *
	 * @return duration as string
	 */
	String getFormattedDuration();

	/**
	 * get the id of the jobstatus
	 *
	 * @return id
	 */
	int getId();

	/**
	 * Returns the current status message for the job
	 *
	 * @return message
	 */
	String getMessage();

	/**
	 * returns the current progress estimate for the job, between 0 and 1
	 *
	 * @return progress estimate
	 */
	float getProgress();

	/**
	 * returns the current progress estimate for the job, as percentage between 0 and 100
	 *
	 * @return progress estimate percentage
	 */
	int getProgressInt();

	/**
	 * returns the start time of the job
	 *
	 * @return start time
	 */
	Instant getStartTime();

	/**
	 * Fetches the current "step" for the job, the stage it is at
	 *
	 * @return stage of job
	 */
	int getStep();

	/**
	 * returns whether the job has finished
	 *
	 * @return finished
	 */
	boolean isFinished();
}
