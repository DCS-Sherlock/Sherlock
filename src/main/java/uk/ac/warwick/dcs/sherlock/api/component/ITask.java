package uk.ac.warwick.dcs.sherlock.api.component;

import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;

import java.util.*;

public interface ITask {

	Class<? extends IDetector> getDetector();

	IJob getJob();

	Map<String, Float> getParameterMapping();

	long getPersistentId();

	List<AbstractModelTaskRawResult> getRawResults();

	void setRawResults(List<AbstractModelTaskRawResult> rawResults);

	/**
	 * Returns the status of the task
	 *
	 * @return the stored status
	 */
	WorkStatus getStatus();

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
