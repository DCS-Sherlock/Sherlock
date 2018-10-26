package uk.ac.warwick.dcs.sherlock.api.common;

import uk.ac.warwick.dcs.sherlock.api.model.IDetector;

/**
 * Static access wrapper for the internal registry, should be used on initialisation to add components into the engine
 */
public class SherlockRegistry {

	private static IRegistry registry;

	/**
	 * Registers an {@link IDetector} implementation to Sherlock
	 * @param detector the implementation
	 * @return was successful?
	 */
	public static Boolean registerDetector(Class<? extends IDetector> detector) {
		if (registry != null) {
			return registry.registerDetector(detector);
		}
		return null;
	}

}
