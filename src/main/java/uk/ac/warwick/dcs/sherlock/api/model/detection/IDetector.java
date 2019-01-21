package uk.ac.warwick.dcs.sherlock.api.model.detection;

import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;

import java.util.*;

/**
 * Interface for implementing a detection algorithm
 * <br><br>
 * Supports adjustable parameters see {@link uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter}
 */
public interface IDetector<T extends AbstractDetectorWorker> {

	/**
	 * Builds a set of workers on a passed dataset, these workers are executed in parallel to produce the algorithm result
	 *
	 * @param data preprocessed dataset
	 *
	 * @return list of configured workers ready to be executed
	 */
	List<T> buildWorkers(List<ModelDataItem> data);

	/**
	 * Fetches the display name for a detector
	 *
	 * @return the display name
	 */
	String getDisplayName();

	/**
	 * Specify the preprocessors required for this detector.
	 * <br><br>
	 * The individual strategies in the list can be produced using the generic methods {@link PreProcessingStrategy#of(String, Class...)} or {@link PreProcessingStrategy#of(String, boolean, Class...)}
	 * in the interface, or using a fully custom {@link PreProcessingStrategy} class.
	 * <br><br>
	 * The string name of each of the strategies is used as the key reference in the preprocessed dataset given to the {@link IDetector#buildWorkers(List)} method
	 *
	 * @return a list of the required preprocessing strategies
	 */
	List<PreProcessingStrategy> getPreProcessors();

	/**
	 * Detectors are ranked either PRIMARY or SUPPORTING to indicate to Sherlock how to treat their results when combining multiple detectors to increase the quality of the final results
	 *
	 * @return the rank of the detector
	 */
	DetectorRank getRank();
}
