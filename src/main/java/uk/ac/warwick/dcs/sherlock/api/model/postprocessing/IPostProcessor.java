package uk.ac.warwick.dcs.sherlock.api.model.postprocessing;

import java.util.*;

/**
 * Supports adjustable parameters see {@link uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter}
 */
public interface IPostProcessor {

	void loadRawResults(List<AbstractModelTaskRawResult> rawResults);

	/**
	 * Run the post processing and return a data item with the final results in the correct format
	 *
	 * TODO: look to make it possible to support multiple scorers in a single PostProcessor depending on the detector used
	 */
	ModelTaskProcessedResults processResults();
}
