package uk.ac.warwick.dcs.sherlock.model.base.detection;

import org.antlr.v4.runtime.Lexer;
import uk.ac.warwick.dcs.sherlock.api.model.*;
import uk.ac.warwick.dcs.sherlock.model.base.data.ModelResultItem;
import uk.ac.warwick.dcs.sherlock.model.base.lang.JavaLexer;
import uk.ac.warwick.dcs.sherlock.model.base.preprocessing.CommentExtractor;

import java.util.stream.Stream;

public class TestDetector extends AbstractPairwiseDetector {

	@Override
	public AbstractPairwiseDetector.AbstractPairwiseDetectorWorker getAbstractPairwiseDetectorWorker() {
		return new TestDetectorWorker();
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

	public class TestDetectorWorker extends AbstractPairwiseDetectorWorker {

		@Override
		public Class<? extends IModelResultItem> getResultItemClass() {
			return ModelResultItem.class;
		}

		@Override
		public void run() {
			this.result.addPairedBlocks(IContentBlock.of(this.file1.getFile(), 1, 2), IContentBlock.of(this.file2.getFile(), 1, 2), 1);
		}
	}
}
