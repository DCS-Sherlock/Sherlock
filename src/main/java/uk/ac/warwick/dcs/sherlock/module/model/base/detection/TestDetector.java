package uk.ac.warwick.dcs.sherlock.module.model.base.detection;

import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter;
import uk.ac.warwick.dcs.sherlock.api.model.detection.PairwiseDetector;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.VariableExtractor;

public class TestDetector extends PairwiseDetector<TestDetectorWorker> {

	@AdjustableParameter (name = "Test Param", defaultValue = 0, minimumBound = 0, maxumumBound = 10, step = 1, description = "A test parameter which is not used")
	public int testParam;

	public TestDetector() {
		super("Test Detector Base", TestDetectorWorker.class, PreProcessingStrategy.of("variables", VariableExtractor.class));
	}
}
