package uk.ac.warwick.dcs.sherlock.api.component;

import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;

import java.util.*;

/**
 * Interface for object which stores an individual detector used in a detection job, including the parameter mapping for the specific task.
 */
public interface ITask {

	/**
	 * Fetches the detector for this task
	 * @return detector class
	 */
	Class<? extends IDetector> getDetector();

	/**
	 * Get the job this task is a child of
	 * @return job instance
	 */
	IJob getJob();

	/**
	 * Gets the mapping of the adjustable parameters for this detector run
	 * @return mapping of reference string to value
	 */
	Map<String, Float> getParameterMapping();

	/**
	 * Returns the unique id for this task
	 * @return id
	 */
	long getPersistentId();

	/**
	 * Returns the raw results object produced by the tasks detector
	 * @return list of the raw results
	 */
	List<AbstractModelTaskRawResult> getRawResults();

	/**
	 * Sets the raw results to be stored and processed
	 * @param rawResults raw results list
	 */
	void setRawResults(List<AbstractModelTaskRawResult> rawResults);

	/**
	 * Returns the status of the task
	 *
	 * @return the stored status
	 */
	WorkStatus getStatus();

	/**
	 * Does the task have raw results saved?
	 * @return has results
	 */
	boolean hasResults();

	/**
	 * Sets the passed adjustable parameter to its default value
	 *
	 * @param paramObj The parameter object to reset
	 *
	 * @return true if successfully reset
	 */
	boolean resetParameter(AdjustableParameterObj paramObj);

	/**
	 * Used to set task complete if no results are found
	 */
	void setComplete();

	/**
	 * Sets the passed adjustable parameter to the passed value
	 * <br><br>
	 * Will return false if the AdjustableParameter is invalid
	 *
	 * @param paramObj The parameter object to set
	 * @param value    new value of the object, if it should be an integer then this will be checked
	 *
	 * @return true if successfully set
	 */
	boolean setParameter(AdjustableParameterObj paramObj, float value);
}
