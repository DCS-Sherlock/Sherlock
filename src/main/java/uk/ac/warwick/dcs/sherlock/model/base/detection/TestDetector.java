package uk.ac.warwick.dcs.sherlock.model.base.detection;

import org.antlr.v4.runtime.Lexer;
import uk.ac.warwick.dcs.sherlock.api.core.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.model.AbstractPairwiseDetector;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.api.model.Language;
import uk.ac.warwick.dcs.sherlock.api.model.data.IContentBlock;
import uk.ac.warwick.dcs.sherlock.model.base.lang.JavaLexer;
import uk.ac.warwick.dcs.sherlock.model.base.preprocessing.CommentExtractor;

import java.util.Collections;
import java.util.List;

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
	public List<IPreProcessingStrategy> getPreProcessors() {
		return Collections.singletonList(IPreProcessingStrategy.of("comments", CommentExtractor.class));
	}

	@Override
	public List<Language> getSupportedLanguages() {
		return Collections.singletonList(Language.JAVA);
	}

	public class TestDetectorWorker extends AbstractPairwiseDetectorWorker {

		@Override
		public void run() {
			for (IndexedString checkLine : this.file1.getPreProcessedLines("comments")) {
				this.file2.getPreProcessedLines("comments").stream().filter(x -> x.valueEquals(checkLine)).forEach(x -> this.result
						.addPairedBlocks(IContentBlock.of(this.file1.getFile(), checkLine.getKey(), checkLine.getKey()), IContentBlock.of(this.file2.getFile(), x.getKey(), x.getKey()), 1));
			}
		}
	}
}
