package uk.ac.warwick.dcs.sherlock.module.model.base.detection;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.model.AbstractPairwiseDetector;
import uk.ac.warwick.dcs.sherlock.api.model.AbstractPostProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.api.model.Language;
import uk.ac.warwick.dcs.sherlock.api.util.IndexedString;
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.JavaLexer;
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.JavaParser;
import uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing.VariableExtractor;

import java.util.*;

public class TestDetector extends AbstractPairwiseDetector {

	private static final Language[] languages = { Language.JAVA };

	@DetectorParameter (name = "Test Param", defaultValue = 0, minimumBound = 0, maxumumBound = 10, step = 1)
	public int testParam;

	@Override
	public AbstractPairwiseDetector.AbstractPairwiseDetectorWorker getAbstractPairwiseDetectorWorker() {
		return new TestDetectorWorker();
	}

	@Override
	public String getDisplayName() {
		return "Test Detector";
	}

	@Override
	public Rank getRank() {
		return Rank.PRIMARY;
	}

	@Override
	public Class<? extends Lexer> getLexer(Language lang) {
		return JavaLexer.class;
	}

	@Override
	public Class<? extends Parser> getParser(Language lang) {
		return JavaParser.class;
	}

	@Override
	public Class<? extends AbstractPostProcessor> getPostProcessor() {
		return null;
	}

	@Override
	public List<IPreProcessingStrategy> getPreProcessors() {
		return Collections.singletonList(IPreProcessingStrategy.of("comments", VariableExtractor.class));
	}

	@Override
	public Language[] getSupportedLanguages() {
		return languages;
	}

	public class TestDetectorWorker extends AbstractPairwiseDetectorWorker {

		@Override
		public void execute() {
			for (IndexedString checkLine : this.file1.getPreProcessedLines("comments")) {
				System.out.println(checkLine);
				//this.file2.getPreProcessedLines("comments").stream().filter(x -> x.valueEquals(checkLine)).forEach(x -> this.result.addPairedBlocks(IContentBlock.of(this.file1.getFile(), checkLine.getKey(), checkLine.getKey()), IContentBlock.of(this.file2.getFile(), x.getKey(), x.getKey()), 1, 1));

			}
		}
	}
}
