package uk.ac.warwick.dcs.sherlock.api.common;

import uk.ac.warwick.dcs.sherlock.api.model.IDetector;

public class SherlockRegistry {

	private static IRegistry registry;

	public static Boolean registerDetector(Class<? extends IDetector> detector) {
		if (registry != null) {
			return registry.registerDetector(detector);
		}
		return null;
	}

}
