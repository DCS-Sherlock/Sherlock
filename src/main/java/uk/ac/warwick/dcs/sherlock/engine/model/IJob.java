package uk.ac.warwick.dcs.sherlock.engine.model;

import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
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
	 * @return the unique id for the job
	 */
	long getPersistentId();

	/**
 	 * @return the list of tasks used to process this job
	 */
	List<ITask> getTasks();

	/**
	 * @return the ids of the files used for this job
	 */
	long[] getFiles();

	/**
	 * @return Timestamp when the job was created;
	 */
	LocalDateTime getTimestamp();

	/**
	 * @return get the workspace containing the job
	 */
	IWorkspace getWorkspace();

	/**
	 * @return get the latest processed results for this job
	 */
	void getResults();

	/**
	 * @return has the prepare() method been called?
	 */
	boolean isPrepared();

	/**
	 * Builds the tasks required for the job, it cannot be edited after this method is called
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
	 * Sets the passed detector adjustable parameter to its default value
	 *
	 * @param paramObj The parameter object to reset
	 *
	 * @return true if successfully reset
	 */
	boolean resetParameter(AdjustableParameterObj paramObj);

	/**
	 * Sets the passed detector adjustable parameter to the passed value
	 * <p><p>
	 * Will return false if the AdjustableParameter is invalid
	 *
	 * @param paramObj The parameter object to set
	 * @param value    new value of the object, if it should be an integer then this will be checked
	 *
	 * @return true if successfully set
	 */
	boolean setParameter(AdjustableParameterObj paramObj, float value);
}
