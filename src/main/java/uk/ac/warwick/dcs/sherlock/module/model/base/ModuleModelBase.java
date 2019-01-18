package uk.ac.warwick.dcs.sherlock.module.model.base;

import uk.ac.warwick.dcs.sherlock.api.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.annotation.EventHandler;
import uk.ac.warwick.dcs.sherlock.api.annotation.SherlockModule;
import uk.ac.warwick.dcs.sherlock.api.event.EventInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.NGramDetector;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.TestDetector;
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.JavaLexer;
import uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing.NGramPostProcessor;
import uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing.NGramRawResult;
import uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing.SimpleObjectEqualityPostProcessor;
import uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing.SimpleObjectEqualityRawResult;

@SherlockModule
public class ModuleModelBase {

	@EventHandler
	public void initialisation(EventInitialisation event) {

		//Test detector registration
		SherlockRegistry.registerDetector(TestDetector.class);
		SherlockRegistry.registerPostProcessor(SimpleObjectEqualityPostProcessor.class, SimpleObjectEqualityRawResult.class);

		SherlockRegistry.registerDetector(NGramDetector.class);
		SherlockRegistry.registerPostProcessor(NGramPostProcessor.class, NGramRawResult.class);

	}

	@EventHandler
	public void preInitialisation(EventPreInitialisation event) {
		SherlockRegistry.registerLanguage("Java", JavaLexer.class);
	}

}
