package uk.ac.warwick.dcs.sherlock.api;

import uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;

public interface IRegistry {

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

	/**
	 * Get correct instance of IPostProcessor to process an AbstractModelTaskRawResult object
	 * @param rawClass class
	 * @return new instance of correct postprocessor
	 */
	IPostProcessor getPostProcessorInstance(Class<? extends AbstractModelTaskRawResult> rawClass);

}
