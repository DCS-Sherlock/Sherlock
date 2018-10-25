package uk.ac.warwick.dcs.sherlock.module.model.base;

import uk.ac.warwick.dcs.sherlock.api.annotations.EventHandler;
import uk.ac.warwick.dcs.sherlock.api.annotations.SherlockModule;
import uk.ac.warwick.dcs.sherlock.api.common.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.event.EventInitialisation;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.TestDetector;

@SherlockModule
public class ModuleModelBase {

	@EventHandler
	public void initialisation(EventInitialisation event) {
		SherlockRegistry.registerDetector(TestDetector.class);
	}

}
