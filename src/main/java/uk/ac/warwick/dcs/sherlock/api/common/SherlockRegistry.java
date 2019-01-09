package uk.ac.warwick.dcs.sherlock.api.common;

import uk.ac.warwick.dcs.sherlock.api.model.IDetector;
import uk.ac.warwick.dcs.sherlock.api.model.IPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.data.AbstractModelRawResult;

/**
 * Static access wrapper for the internal registry, should be used on initialisation to add components into the engine
 */
public class SherlockRegistry {

	private static IRegistry registry;

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

	public static boolean registerPostProcessor(Class<? extends IPostProcessor> postProcessor, Class<? extends AbstractModelRawResult> handledResultTypes) {
		if (registry != null) {
			return registry.registerPostProcessor(postProcessor, handledResultTypes);
		}
		return false;
	}
}
