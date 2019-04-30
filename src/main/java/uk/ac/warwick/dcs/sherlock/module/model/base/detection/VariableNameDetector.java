package uk.ac.warwick.dcs.sherlock.module.model.base.detection;

import uk.ac.warwick.dcs.sherlock.api.model.detection.PairwiseDetector;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.VariableExtractor;

public class VariableNameDetector extends PairwiseDetector<VariableNameDetectorWorker> {

	/*@AdjustableParameter (name = "Test Param", defaultValue = 0, minimumBound = 0, maxumumBound = 10, step = 1, description = "A test parameter which is not used")
	public int testParam;*/

	public VariableNameDetector() {
		super("Variable Name Detector", "Detector which scores files based on how many variables are exactly duplicated between them", VariableNameDetectorWorker.class, PreProcessingStrategy.of("variables", VariableExtractor.class));
	}
}
