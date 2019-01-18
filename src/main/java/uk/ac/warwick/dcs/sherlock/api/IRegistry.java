package uk.ac.warwick.dcs.sherlock.api;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector.Rank;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;

import java.util.*;

public interface IRegistry {

	/**
	 * Get the description of a detector
	 *
	 * @param det detector class
	 *
	 * @return description of the detector
	 */
	String getDetecorDescription(Class<? extends IDetector> det);

	/**
	 * Get the adjustable parameters for a detector
	 *
	 * @param det detector class
	 *
	 * @return adjustable parameters for the detector class
	 */
	List<AdjustableParameterObj> getDetectorAdjustableParameters(Class<? extends IDetector> det);

	/**
	 * Get the display name of the detector
	 *
	 * @param det detector class
	 *
	 * @return display name of the detector
	 */
	String getDetectorDisplayName(Class<? extends IDetector> det);

	/**
	 * Get the languages supported by the detector
	 *
	 * @param det detector class
	 *
	 * @return languages supported by the detector
	 */
	@Deprecated
	Language[] getDetectorLanguages(Class<? extends IDetector> det);

	/**
	 * Get the rank of the detector (Should it be a primary result, or just a reinforcing result (secondary))
	 *
	 * @param det detector class
	 *
	 * @return the detector rank
	 */
	Rank getDetectorRank(Class<? extends IDetector> det);

	/**
	 * Fetch the set of all detectors registered to Sherlock
	 *
	 * @return a set of all detectors registered
	 */
	Set<Class<? extends IDetector>> getDetectors();

	/**
	 * Returns a set of all detectors registered which support the language specified
	 *
	 * @param language the language to search
	 *
	 * @return the set of detectors
	 */
	@Deprecated
	Set<Class<? extends IDetector>> getDetectors(Language language);

	/**
	 * Gets the adjustable parameters for a postprocessor
	 *
	 * @param postProcessor postprocessor class
	 *
	 * @return the list of adjustable parameters
	 */
	List<AdjustableParameterObj> getPostProcessorAdjustableParameters(Class<? extends IPostProcessor> postProcessor);

	/**
	 * Gets the adjustable parameters for the corresponding post processor for a detector
	 *
	 * @param det detector class
	 *
	 * @return the list of adjustable parameters
	 */
	List<AdjustableParameterObj> getPostProcessorAdjustableParametersFromDetector(Class<? extends IDetector> det);

	/**
	 * Get correct instance of IPostProcessor to process an AbstractModelTaskRawResult object
	 *
	 * @param rawClass class
	 *
	 * @return new instance of correct postprocessor
	 */
	IPostProcessor getPostProcessorInstance(Class<? extends AbstractModelTaskRawResult> rawClass);

	/**
	 * Registers an {@link IDetector} implementation to Sherlock.
	 *
	 * @param detector the implementation
	 *
	 * @return was successful?
	 */
	boolean registerDetector(Class<? extends IDetector> detector);

	/**
	 * Register a lexer to the language of the name passed, creating the language if the name is not recognised
	 *
	 * @param name  Name of the language, not case sensitive
	 * @param lexer Lexer implementation for the language
	 *
	 * @return was successful?
	 */
	boolean registerLanguage(String name, Class<? extends Lexer> lexer);

	/**
	 * Registers an {@link IPostProcessor} implementation to Sherlock, associates it with the {@link AbstractModelTaskRawResult} types it will process
	 *
	 * @param postProcessor      the implementation
	 * @param handledResultTypes handled {@link AbstractModelTaskRawResult} type(s) by the implementation
	 *
	 * @return was successful?
	 */
	boolean registerPostProcessor(Class<? extends IPostProcessor> postProcessor, Class<? extends AbstractModelTaskRawResult> handledResultTypes);

	/*boolean registerPreProcessor(Class<? extends IPreProcessor> preProcessor);*/

}
