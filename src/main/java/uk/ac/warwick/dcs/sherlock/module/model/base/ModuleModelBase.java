package uk.ac.warwick.dcs.sherlock.module.model.base;

import uk.ac.warwick.dcs.sherlock.api.registry.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.annotation.EventHandler;
import uk.ac.warwick.dcs.sherlock.api.annotation.SherlockModule;
import uk.ac.warwick.dcs.sherlock.api.event.EventInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.NGramDetector;
import uk.ac.warwick.dcs.sherlock.module.model.base.detection.VariableNameDetector;
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.JavaLexer;
import uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing.NGramPostProcessor;
import uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing.NGramRawResult;
import uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing.SimpleObjectEqualityPostProcessor;
import uk.ac.warwick.dcs.sherlock.module.model.base.postprocessing.SimpleObjectEqualityRawResult;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.*;

@SherlockModule
public class ModuleModelBase {

	@EventHandler
	public void initialisation(EventInitialisation event) {
		SherlockRegistry.registerGeneralPreProcessor(CommentExtractor.class);
		SherlockRegistry.registerGeneralPreProcessor(CommentRemover.class);
		SherlockRegistry.registerGeneralPreProcessor(TrimWhitespaceOnly.class);
		SherlockRegistry.registerAdvancedPreProcessorImplementation("uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.VariableExtractor", VariableExtractorJava.class);

		SherlockRegistry.registerDetector(VariableNameDetector.class);
		SherlockRegistry.registerPostProcessor(SimpleObjectEqualityPostProcessor.class, SimpleObjectEqualityRawResult.class);

		SherlockRegistry.registerDetector(NGramDetector.class);
		SherlockRegistry.registerPostProcessor(NGramPostProcessor.class, NGramRawResult.class);

	}

	@EventHandler
	public void preInitialisation(EventPreInitialisation event) {
		SherlockRegistry.registerLanguage("Java", JavaLexer.class);
		//SherlockRegistry.registerLanguage("Haskell", HaskellLexer.class); -- found in Sherlock-Extra

		SherlockRegistry.registerAdvancedPreProcessorGroup(VariableExtractor.class);
	}

}
