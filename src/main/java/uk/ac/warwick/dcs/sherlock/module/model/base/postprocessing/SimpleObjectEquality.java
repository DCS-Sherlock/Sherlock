package uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing;

import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.ModelTaskProcessedResults;
import uk.ac.warwick.dcs.sherlock.module.model.base.scoring.SimpleObjectEqualityScorer;

import java.util.*;

public class SimpleObjectEquality implements IPostProcessor {

	@AdjustableParameter (name = "Test Param", defaultValue = 0, minimumBound = 0, maxumumBound = 10, step = 1)
	public int testParam;

	@Override
	public void loadRawResults(List<AbstractModelTaskRawResult> rawResults) {
		//store the list of raw results, maybe do some initial processing on them
	}

	@Override
	public ModelTaskProcessedResults processResults() {
		ModelTaskProcessedResults results = new ModelTaskProcessedResults(new SimpleObjectEqualityScorer());

		// do stuff in here

		// see docs, use:
		// x = results.addGroup();
		// x.addCodeBlock(..........); cont..

		return results;
	}
}
