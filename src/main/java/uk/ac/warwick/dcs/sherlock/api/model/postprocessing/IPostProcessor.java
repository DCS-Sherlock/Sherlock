package uk.ac.warwick.dcs.sherlock.api.model.postprocessing;

import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile;

import java.util.*;

/**
 * Post process the raw results into the Sherlock formal data storage structure
 *
 * Supports adjustable parameters see {@link uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter}
 */
public interface IPostProcessor<T extends AbstractModelTaskRawResult> {

	/**
	 * Run the post processing and return a data item with the final results in the correct format
	 *
	 * See wiki for implementation hints
	 *
	 * use "ModelTaskProcessedResults results = new ModelTaskProcessedResults();" to create new return object
	 *
	 * @param files      the list of files covered by the rawResults passed
	 * @param rawResults the set of rawResults produced by the IDetector
	 *
	 * @return populated processed results object
	 */
	ModelTaskProcessedResults processResults(List<ISourceFile> files, List<T> rawResults);
}
