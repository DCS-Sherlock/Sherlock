package uk.ac.warwick.dcs.sherlock.api.common;

import uk.ac.warwick.dcs.sherlock.api.model.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.data.IModelRawResult;

public interface IRegistry {

	/**
	 * Registers an {@link IDetector} implementation to Sherlock
	 * @param detector the implementation
	 * @return was successful?
	 */
	boolean registerDetector(Class<? extends IDetector> detector);

	/**
	 * Registers an {@link IPostProcessor} implementation to Sherlock, associates it with the {@link IModelRawResult} types it will process
	 * @param postProcessor the implementation
	 * @param handledResultTypes handled {@link IModelRawResult} type(s) by the implementation
	 * @return was successful?
	 */
	boolean registerPostProcessor(Class<? extends IPostProcessor> postProcessor, Class<? extends IModelRawResult> handledResultTypes);

}
