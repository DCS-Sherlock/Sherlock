package uk.ac.warwick.dcs.sherlock.api;

import uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameterObj;
import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;

import java.util.*;

public interface IRegistry {

	/**
	 *
	 * @param det detector class
	 * @return description of the detector
	 */
	String getDetecorDescription(Class<? extends IDetector> det);

	/**
	 *
	 * @param det detector class
	 * @return adjustable parameters for the detector class
	 */
	List<AdjustableParameterObj> getDetectorAdjustableParameters(Class<? extends IDetector> det);

	/**
	 *
	 * @param det detector class
	 * @return languages supported by the detector
	 */
	String getDetectorDisplayName(Class<? extends IDetector> det);

	/**
	 *
	 * @param det detector class
	 * @return display name of the detector
	 */
	Language[] getDetectorLanguages(Class<? extends IDetector> det);

	/**
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
	Set<Class<? extends IDetector>> getDetectors(Language language);

	/**
	 * Get correct instance of IPostProcessor to process an AbstractModelTaskRawResult object
	 *
	 * @param rawClass class
	 *
	 * @return new instance of correct postprocessor
	 */
	IPostProcessor getPostProcessorInstance(Class<? extends AbstractModelTaskRawResult> rawClass);

	/**
	 * Registers an {@link IDetector} implementation to Sherlock
	 *
	 * @param detector the implementation
	 *
	 * @return was successful?
	 */
	boolean registerDetector(Class<? extends IDetector> detector);

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
