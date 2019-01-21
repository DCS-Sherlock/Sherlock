package uk.ac.warwick.dcs.sherlock.api;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectorRank;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IAdvancedPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IAdvancedPreProcessorGroup;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IGeneralPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.PreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.util.*;

/**
 * Static access wrapper for the internal registry, should be used on initialisation to add components into the engine
 */
public class SherlockRegistry {

	private static IRegistry registry;

	/**
	 * Fetches a tuple containing the correct AdvancedPreProcessor and Lexer implementations for this group/language combination
	 *
	 * @param group    the group to find a valid preprocessor for
	 * @param language the language string in use, should have been already validated to work with this group
	 *
	 * @return the tuple
	 */
	public static ITuple<Class<? extends IAdvancedPreProcessor>, Class<? extends Lexer>> getAdvancedPostProcessorForLanguage(Class<? extends IAdvancedPreProcessorGroup> group, String language) {
		if (registry != null) {
			return registry.getAdvancedPostProcessorForLanguage(group, language);
		}

		return null;
	}

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
	public static Set<String> getDetectorLanguages(Class<? extends IDetector> det) {
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
	public static DetectorRank getDetectorRank(Class<? extends IDetector> det) {
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
	public static Set<Class<? extends IDetector>> getDetectors(String language) {
		if (registry != null) {
			return registry.getDetectors(language);
		}
		return null;
	}

	/**
	 * Returns the set of registered languages
	 *
	 * @return set of languages
	 */
	public static Set<String> getLanguages() {
		if (registry != null) {
			return registry.getLanguages();
		}
		return null;
	}

	/**
	 * Finds and returns a valid lexer for the preprocessing stragety, if one cannot be found returns null
	 *
	 * @param strategy the PreProcessingStrategy
	 * @param language the language string to prcess
	 *
	 * @return valid lexer or null
	 */
	public static Class<? extends Lexer> getLexerForStrategy(PreProcessingStrategy strategy, String language) {
		if (registry != null) {
			return registry.getLexerForStrategy(strategy, language);
		}
		return null;
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
	public static List<AdjustableParameterObj> getPostProcessorAdjustableParametersFromDetector(Class<? extends IDetector> det) {
		if (registry != null) {
			return registry.getPostProcessorAdjustableParametersFromDetector(det);
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
	 * Registers a grouping for {@link IAdvancedPreProcessor} to Sherlock. These groups of multiple Advanced PreProcessors all perform the same function, for different languages, laxers and parsers
	 *
	 * @param preProcessorGroup the group
	 *
	 * @return was successful?
	 */
	public static boolean registerAdvancedPreProcessorGroup(Class<? extends IAdvancedPreProcessorGroup> preProcessorGroup) {
		if (registry != null) {
			return registry.registerAdvancedPreProcessorGroup(preProcessorGroup);
		}
		return false;
	}

	/**
	 * Registers an {@link IAdvancedPreProcessor} implementation to a group
	 *
	 * @param groupClassPath the ClassPath for the group object to register to. The preProcessor MUST perform the groups assigned function, this cannot be checked!!!
	 * @param preProcessor   the implementation
	 *
	 * @return was successful?
	 */
	public static boolean registerAdvancedPreProcessorImplementation(String groupClassPath, Class<? extends IAdvancedPreProcessor> preProcessor) {
		if (registry != null) {
			return registry.registerAdvancedPreProcessorImplementation(groupClassPath, preProcessor);
		}
		return false;
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
	 * Registers an {@link IGeneralPreProcessor} implementation to Sherlock
	 *
	 * @param preProcessor the implementation
	 *
	 * @return was successful?
	 */
	public static boolean registerGeneralPreProcessor(Class<? extends IGeneralPreProcessor> preProcessor) {
		if (registry != null) {
			return registry.registerGeneralPreProcessor(preProcessor);
		}
		return false;
	}

	/**
	 * Register a lexer to the language of the name passed, creating the language if the name is not recognised
	 *
	 * @param name  Name of the language, not case sensitive
	 * @param lexer Lexer implementation for the language
	 *
	 * @return was successful?
	 */
	public static boolean registerLanguage(String name, Class<? extends Lexer> lexer) {
		if (registry != null) {
			return registry.registerLanguage(name, lexer);
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
}
