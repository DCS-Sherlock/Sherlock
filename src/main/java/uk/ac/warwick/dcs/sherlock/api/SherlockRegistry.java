package uk.ac.warwick.dcs.sherlock.api;

import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector.Rank;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;

import java.util.*;

/**
 * Static access wrapper for the internal registry, should be used on initialisation to add components into the engine
 */
public class SherlockRegistry {

	private static IRegistry registry;

	/**
	 * @param det detector class
	 *
	 * @return description of the detector
	 */
	public static String getDetecorDescription(Class<? extends IDetector> det) {
		if (registry != null) {
			return registry.getDetecorDescription(det);
		}
		return null;
	}

	/**
	 * @param det detector class
	 *
	 * @return adjustable parameters for the detector class
	 */
	public static List<AdjustableParameterObj> getDetectorAdjustableParameters(Class<? extends IDetector> det) {
		if (registry != null) {
			return registry.getDetectorAdjustableParameters(det);
		}
		return null;
	}

	/**
	 * @param det detector class
	 *
	 * @return display name of the detector
	 */
	public static String getDetectorDisplayName(Class<? extends IDetector> det) {
		if (registry != null) {
			return registry.getDetectorDisplayName(det);
		}
		return null;
	}

	/**
	 * @param det detector class
	 *
	 * @return languages supported by the detector
	 */
	public static Language[] getDetectorLanguages(Class<? extends IDetector> det) {
		if (registry != null) {
			return registry.getDetectorLanguages(det);
		}
		return null;
	}

	/**
	 * @param det detector class
	 *
	 * @return the detector rank
	 */
	public static Rank getDetectorRank(Class<? extends IDetector> det) {
		if (registry != null) {
			return registry.getDetectorRank(det);
		}
		return null;
	}

	/**
	 * @return a set of all detectors registered
	 */
	public static Set<Class<? extends IDetector>> getDetectors() {
		if (registry != null) {
			return registry.getDetectors();
		}
		return null;
	}

	/**
	 * Returns a set of all detectors registered which support the language specified
	 *
	 * @param language the language to search
	 *
	 * @return the set of detectors
	 */
	public static Set<Class<? extends IDetector>> getDetectors(Language language) {
		if (registry != null) {
			return registry.getDetectors(language);
		}
		return null;
	}

	/**
	 * Fetches the correct {@link IPostProcessor} instance for the raw result type
	 *
	 * @param rawClass type to search
	 *
	 * @return new instance of the correct postprocessor
	 */
	public static IPostProcessor getPostProcessorInstance(Class<? extends AbstractModelTaskRawResult> rawClass) {
		if (registry != null) {
			return registry.getPostProcessorInstance(rawClass);
		}
		return null;
	}

	/**
	 * Registers an {@link IDetector} implementation to Sherlock
	 *
	 * @param detector the implementation
	 *
	 * @return was successful?
	 */
	public static boolean registerDetector(Class<? extends IDetector> detector) {
		if (registry != null) {
			return registry.registerDetector(detector);
		}
		return false;
	}

	/**
	 * Associates an {@link IPostProcessor} with a result type [{@link AbstractModelTaskRawResult}]
	 *
	 * @param postProcessor      the PostProcessor
	 * @param handledResultTypes the data type for the post processor
	 *
	 * @return was sucessful?
	 */
	public static boolean registerPostProcessor(Class<? extends IPostProcessor> postProcessor, Class<? extends AbstractModelTaskRawResult> handledResultTypes) {
		if (registry != null) {
			return registry.registerPostProcessor(postProcessor, handledResultTypes);
		}
		return false;
	}

	/**
	 * Gets the adjustable parameters for a postprocessor
	 *
	 * @param postProcessor postprocessor class
	 *
	 * @return the list of adjustable parameters
	 */
	public static List<AdjustableParameterObj> getPostProcessorAdjustableParameters(Class<? extends IPostProcessor> postProcessor) {
		if (registry != null) {
			return registry.getPostProcessorAdjustableParameters(postProcessor);
		}
		return null;
	}

	/**
	 * Gets the adjustable parameters for the corresponding post processor for a detector
	 *
	 * @param det detector class
	 *
	 * @return the list of adjustable parameters
	 */
	public static List<AdjustableParameterObj> getPostProcessorAdjustableParametersFromDetector(Class<? extends IDetector> det){
		if (registry != null) {
			return registry.getPostProcessorAdjustableParametersFromDetector(det);
		}
		return null;
	}
}
