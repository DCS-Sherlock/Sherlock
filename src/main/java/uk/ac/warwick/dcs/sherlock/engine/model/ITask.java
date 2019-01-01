package uk.ac.warwick.dcs.sherlock.engine.model;

import uk.ac.warwick.dcs.sherlock.api.model.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.data.AbstractModelRawResult;

import java.util.*;

public interface ITask {

	Class<? extends IDetector> getDetector();

	IJob getJob();

	long getPersistentId();

	IDetector.Rank getRank();

	List<AbstractModelRawResult> getRawResults();

	void setRawResults(List<AbstractModelRawResult> rawResults);
}
