package uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing;

import uk.ac.warwick.dcs.sherlock.api.model.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.data.IModelRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.data.ModelProcessedResults;

import java.util.*;

public class SimpleObjectEquality implements IPostProcessor {

	@Override
	public void loadRawResults(List<IModelRawResult> results) {

	}

	@Override
	public ModelProcessedResults processResults() {
		return null;
	}
}
