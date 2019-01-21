package uk.ac.warwick.dcs.sherlock.engine.executor.work;

import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.ModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;

import java.util.*;

public interface IWorkTask {

	void addModelDataItem(ModelDataItem item);

	Class<? extends IDetector> getDetector();

	String getLanguage();

	List<PreProcessingStrategy> getPreProcessingStrategies();

}
