package uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing;

import uk.ac.warwick.dcs.sherlock.api.model.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.data.IModelProcessedResults;
import uk.ac.warwick.dcs.sherlock.api.model.data.AbstractModelRawResult;

import java.util.*;

public class SimpleObjectEquality implements IPostProcessor {

	@Override
	public void loadRawResults(List<AbstractModelRawResult> results) {

	}

	@Override
	public List<IModelProcessedResults> processResults() {
		return null;
	}
}
