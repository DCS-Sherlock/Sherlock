package uk.ac.warwick.dcs.sherlock.module.model.base.detection;

import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter;
import uk.ac.warwick.dcs.sherlock.api.common.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.model.detection.AbstractPairwiseDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.AbstractPairwiseDetectorWorker;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectorRank;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.TestDetector.TestDetectorWorker;
import uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing.SimpleObjectEqualityRawResult;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.VariableExtractor;

import java.util.*;

public class TestDetector extends AbstractPairwiseDetector<TestDetectorWorker> {

	@AdjustableParameter (name = "Test Param", defaultValue = 0, minimumBound = 0, maxumumBound = 10, step = 1)
	public int testParam;

	@Override
	public TestDetectorWorker getAbstractPairwiseDetectorWorker() {
		return new TestDetector.TestDetectorWorker();
	}

	@Override
	public String getDisplayName() {
		return "Test Detector";
	}

	@Override
	public List<PreProcessingStrategy> getPreProcessors() {
		return Collections.singletonList(PreProcessingStrategy.of("variables", VariableExtractor.class));
	}

	@Override
	public DetectorRank getRank() {
		return DetectorRank.PRIMARY;
	}

	public class TestDetectorWorker extends AbstractPairwiseDetectorWorker<SimpleObjectEqualityRawResult<String>> {

		@Override
		public void execute() {
			// This detector finds and matches up variables - it only works on declarations of the variable, not every time the variable is called.

			List<IndexedString> linesF1 = this.file1.getPreProcessedLines("variables");
			List<IndexedString> linesF2 = this.file2.getPreProcessedLines("variables");

			List<Integer> usedIndexesF2 = new LinkedList<>();

			SimpleObjectEqualityRawResult<String> res = new SimpleObjectEqualityRawResult<>(this.file1.getFile(), this.file2.getFile(), linesF1.size(), linesF2.size());

			for (IndexedString checkLine : linesF1) {
				linesF2.stream().filter(x -> x.valueEquals(checkLine)).forEach(x -> res.put(checkLine.getValue(), checkLine.getKey(), x.getKey()));
			}

			this.result = res;
		}
	}
}
