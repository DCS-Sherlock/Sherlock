package uk.ac.warwick.dcs.sherlock.api;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector.Rank;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IAdvancedPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IAdvancedPreProcessorGroup;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IGeneralPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;
import uk.ac.warwick.dcs.sherlock.api.util.ITuple;

import java.util.*;

public interface IRegistry {

	/**
	 * Fetches a tuple containing the correct AdvancedPreProcessor and Lexer implementations for this group/language combination
	 *
	 * @param group    the group to find a valid preprocessor for
	 * @param language the language string in use, should have been already validated to work with this group
	 *
	 * @return the tuple
	 */
	ITuple<Class<? extends IAdvancedPreProcessor>, Class<? extends Lexer>> getAdvancedPostProcessorForLanguage(Class<? extends IAdvancedPreProcessorGroup> group, String language);

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
	 * Registers a grouping for {@link IAdvancedPreProcessor} to Sherlock. These groups of multiple Advanced PreProcessors all perform the same function, for different languages, laxers and parsers
	 *
	 * @param preProcessorGroup the group
	 *
	 * @return was successful?
	 */
	boolean registerAdvancedPreProcessorGroup(Class<? extends IAdvancedPreProcessorGroup> preProcessorGroup);

	/**
	 * Registers an {@link IAdvancedPreProcessor} implementation to a group
	 *
	 * @param groupClassPath the ClassPath for the group object to register to. The preProcessor MUST perform the groups assigned function, this cannot be checked!!!
	 * @param preProcessor   the implementation
	 *
	 * @return was successful?
	 */
	boolean registerAdvancedPreProcessorImplementation(String groupClassPath, Class<? extends IAdvancedPreProcessor> preProcessor);

	/**
	 * Registers an {@link IDetector} implementation to Sherlock. The presence of any dependencies should ideally tested before calling this method, one way to do this is to use the {@link
	 * uk.ac.warwick.dcs.sherlock.engine.SherlockEngine#isModulePresent(String)}
	 *
	 * @param detector the implementation
	 *
	 * @return was successful?
	 */
	boolean registerDetector(Class<? extends IDetector> detector);

	/**
	 * Registers an {@link IGeneralPreProcessor} implementation to Sherlock
	 *
	 * @param preProcessor the implementation
	 *
	 * @return was successful?
	 */
	boolean registerGeneralPreProcessor(Class<? extends IGeneralPreProcessor> preProcessor);

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

}
