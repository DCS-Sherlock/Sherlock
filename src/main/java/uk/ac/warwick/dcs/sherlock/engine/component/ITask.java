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

	void setRawResults(List<AbstractModelTaskRawResult> rawResults);

	/**
	 * Returns the status of the task
	 *
	 * @return the stored status
	 */
	WorkStatus getStatus();

	/**
	 * Used to set task complete if no results are found
	 */
	void setComplete();
}
