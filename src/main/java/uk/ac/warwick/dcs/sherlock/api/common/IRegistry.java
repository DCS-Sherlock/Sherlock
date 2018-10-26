package uk.ac.warwick.dcs.sherlock.api.common;

import uk.ac.warwick.dcs.sherlock.api.model.IDetector;

public interface IRegistry {

	/**
	 * Registers an {@link IDetector} implementation to Sherlock
	 * @param detector the implementation
	 * @return was successful?
	 */
	Boolean registerDetector(Class<? extends IDetector> detector);

}
