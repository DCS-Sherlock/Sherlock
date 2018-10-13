package uk.ac.warwick.dcs.sherlock.model.base.detection;

import org.antlr.v4.runtime.Lexer;
import uk.ac.warwick.dcs.sherlock.api.model.*;
import uk.ac.warwick.dcs.sherlock.model.base.lang.JavaLexer;
import uk.ac.warwick.dcs.sherlock.model.base.preprocessing.CommentExtractor;

import java.util.stream.Stream;

public class TestDetector implements IDetector {

	@Override
	public int execute(IModelData data, IModelResult result) {

		// Do stuff here
		return 0;
	}

	@Override
	public String getDisplayName() {
		return "Test Detector";
	}

	@Override
	public Class<? extends Lexer> getLexer(Language lang) {
		return JavaLexer.class;
	}

	@Override
	public Stream<Class<? extends IPreProcessor>> getPreProcessors() {
		return Stream.of(CommentExtractor.class);
	}

	@Override
	public Stream<Language> getSupportedLanguages() {
		return Stream.of(Language.JAVA);
	}
}
