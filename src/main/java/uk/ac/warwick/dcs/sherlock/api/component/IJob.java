package uk.ac.warwick.dcs.sherlock.api.component;

import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;

import java.time.LocalDateTime;
import java.util.*;

public interface IJob {

	/**
	 * Add a detector to the job, a job for each detector will be created when the job is prepared
	 *
	 * @param det Class of the detector to add
	 *
	 * @return returns true if added, false if already added to the job or the job has been prepared
	 */
	boolean addDetector(Class<? extends IDetector> det);

	/**
	 * Return a new JobResult instance
	 *
	 * @return newly created instance
	 */
	IResultJob createNewResult();

	/**
	 * The ids of the files used for this job
	 *
	 * @return ids of files used
	 */
	long[] getFiles();

	/**
	 * get the latest processed results for this job
	 *
	 * @return latest processed results
	 */
	IResultJob getLatestResult();

	/**
	 * The unique id for the job
	 *
	 * @return the unique id
	 */
	long getPersistentId();

	/**
	 * Returns the status of the job
	 *
	 * @return the stored status
	 */
	WorkStatus getStatus();

	/**
	 * Sets the status of the job
	 *
	 * @param status the new status of the job
	 */
	void setStatus(WorkStatus status);

	/**
	 * The list of tasks used to process this job
	 *
	 * @return tasks used for the job
	 */
	List<ITask> getTasks();

	/**
	 * Fetch the timestamp for the job creation;
	 *
	 * @return Timestamp when the job was created;
	 */
	LocalDateTime getTimestamp();

	/**
	 * get the workspace containing the job
	 *
	 * @return workspace
	 */
	IWorkspace getWorkspace();

	/**
	 * Has the prepare() method been called?
	 *
	 * @return is prepared?
	 */
	boolean isPrepared();

	/**
	 * Builds the tasks required for the job, it cannot be edited after this method is called
	 * <br><br>
	 * This should also save the job to the database
	 *
	 * @return successfully prepared?
	 */
	boolean prepare();

	/**
	 * Removes a detector from the job
	 *
	 * @param det Class of the detector to remove
	 *
	 * @return returns true if removed, false if not present in the job or the job has been prepared
	 */
	boolean removeDetector(Class<? extends IDetector> det);

	/**
	 * Delete the job, all tasks and results. Cannot be undone
	 */
	void remove();
}
