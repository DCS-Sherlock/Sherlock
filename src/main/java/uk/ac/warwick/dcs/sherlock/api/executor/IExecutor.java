package uk.ac.warwick.dcs.sherlock.api.executor;

import uk.ac.warwick.dcs.sherlock.api.component.IJob;

import java.util.*;

/**
 * Execution Module interface, defines an execution scheme
 */
public interface IExecutor {

	/**
	 * Returns a list of all active, queued or recently finished job statuses
	 * @return list of statuses
	 */
	List<IJobStatus> getAllJobStatuses();

	/**
	 * Returns a list of queued jobs
	 * @return list of jobs
	 */
	List<IJob> getWaitingJobs();

	/**
	 * Gets the job status for a specific job
	 * @param job job to get status of
	 * @return status
	 */
	IJobStatus getJobStatus(IJob job);

	/**
	 * Gets the job from a job status
	 * @param jobStatus job status instance
	 * @return corresponding job
	 */
	IJob getJob(IJobStatus jobStatus);

	/**
	 * shutsdown the executor
	 */
	void shutdown();

	/**
	 * submits a job to this executor
	 * @param job job to submit
	 * @return was successful?
	 */
	boolean submitJob(IJob job);

	/**
	 * removes a finished job from the list of statuses
	 * @param jobStatus status of finished job to remove from list
	 * @return successful
	 */
	boolean dismissJob(IJobStatus jobStatus);

	/**
	 * removes a finished job from the list of statuses
	 * @param job job instance of finished job to remove from list
	 * @return successful
	 */
	boolean dismissJob(IJob job);

	/**
	 * Cancels the execution of a job
	 * @param jobStatus job status of job to cancel
	 * @return successful
	 */
	boolean cancelJob(IJobStatus jobStatus);

	/**
	 * Cancels the execution of a job
	 * @param job job instance to cancel
	 * @return successful
	 */
	boolean cancelJob(IJob job);

}
