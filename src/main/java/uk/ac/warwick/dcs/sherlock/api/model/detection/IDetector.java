package uk.ac.warwick.dcs.sherlock.api.model.detection;

import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;

import java.util.*;

/**
 * Low level interface for implementing a detection algorithm. The Abstract {@link Detector} class should be used over this
 * <br><br>
 * Supports adjustable parameters see {@link uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter}
 */
public interface IDetector<T extends DetectorWorker> {

	/**
	 * Builds a set of workers on a passed dataset, these workers are executed in parallel to produce the algorithm result
	 *
	 * @param data preprocessed dataset
	 *
	 * @return list of configured workers ready to be executed
	 */
	List<T> buildWorkers(List<ModelDataItem> data);

	/**
	 * Fetches the display name for the detector
	 *
	 * @return the display name
	 */
	String getDisplayName();

	/**
	 * Fetches the description string for the detector
	 * @return
	 */
	String getDescription();

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
}
