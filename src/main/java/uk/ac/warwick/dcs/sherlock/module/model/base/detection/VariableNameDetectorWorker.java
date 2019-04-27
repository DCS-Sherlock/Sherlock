package uk.ac.warwick.dcs.sherlock.module.model.base.detection;

import uk.ac.warwick.dcs.sherlock.api.util.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.ModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.detection.PairwiseDetectorWorker;
import uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing.SimpleObjectEqualityRawResult;

import java.util.*;

public class VariableNameDetectorWorker extends PairwiseDetectorWorker<SimpleObjectEqualityRawResult<String>> {

	public VariableNameDetectorWorker(IDetector parent, ModelDataItem file1Data, ModelDataItem file2Data) {
		super(parent, file1Data, file2Data);
	}

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
