package uk.ac.warwick.dcs.sherlock.engine.component;

import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;

import java.util.*;

public interface ITask {

	Class<? extends IDetector> getDetector();

	IJob getJob();

	Map<String, Float> getParameterMapping();

	long getPersistentId();

	IDetector.Rank getRank();

	List<AbstractModelTaskRawResult> getRawResults();

	/**
	 * Returns the status of the task
	 *
	 * @return the stored status
	 */
	WorkStatus getStatus();

	/**
	 * Sets the status of the task
	 *
	 * @param status the new status of the task
	 */
	void setStatus(WorkStatus status);

	void setRawResults(List<AbstractModelTaskRawResult> rawResults);
}
