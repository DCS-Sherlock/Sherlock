package uk.ac.warwick.dcs.sherlock.api.common;

import uk.ac.warwick.dcs.sherlock.api.model.IDetector;

public interface IRegistry {

	Boolean registerDetector(Class<? extends IDetector> detector);

}
