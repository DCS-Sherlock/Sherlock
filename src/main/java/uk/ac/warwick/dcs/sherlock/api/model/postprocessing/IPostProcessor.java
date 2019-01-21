package uk.ac.warwick.dcs.sherlock.api.model.postprocessing;

import uk.ac.warwick.dcs.sherlock.api.common.ISourceFile;

import java.util.*;

/**
 * Supports adjustable parameters see {@link uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter}
 */
public interface IPostProcessor<T extends AbstractModelTaskRawResult> {

	//TODO: look to make it possible to support multiple scorers in a single PostProcessor depending on the detector used

	/**
	 * Run the post processing and return a data item with the final results in the correct format
	 *
	 * @param files      the list of files covered by the rawResults passed
	 * @param rawResults the set of rawResults produced by the IDetector
	 *
	 * @return populated processed results object
	 */
	ModelTaskProcessedResults processResults(List<ISourceFile> files, List<T> rawResults);
}
