package uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing;

import org.antlr.v4.runtime.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.ILexerSpecification;
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.JavaLexer;
import uk.ac.warwick.dcs.sherlock.module.model.base.utils.PreprocessorParamsProvider;
import uk.ac.warwick.dcs.sherlock.module.model.base.utils.TestJavaFile;
import uk.ac.warwick.dcs.sherlock.module.model.base.utils.TestUtils;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentExtractorTest {

	@Test
	void getLexerSpecification() {
		ILexerSpecification ls = (new CommentExtractor()).getLexerSpecification();
		assertTrue(ls instanceof StandardLexerSpecification);
	}

	@ParameterizedTest
	@ArgumentsSource (PreprocessorParamsProvider.class)
	void process(TestJavaFile testJavaFile) throws Exception {
		File file = TestUtils.makeFileWithContents("temp.java", testJavaFile.Original());
		Class<? extends Lexer> lexerClass = JavaLexer.class;
		Lexer lexer = lexerClass.getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromFileName(file.getName()));
		List<? extends Token> tokensMaster = lexer.getAllTokens();
		List<? extends Token> tokens = (new CommentExtractor()).process(tokensMaster, lexer.getVocabulary(), "Java");
		Iterator<? extends Token> t = tokens.iterator();
		List<String> ls = new ArrayList<>();
		while (t.hasNext()) {
			ls.add(t.next().getText());
		}
		FileUtils.forceDelete(file);
		assertEquals(testJavaFile.CommentExtractor(), ls);
	}

	@ParameterizedTest
	@ArgumentsSource (PreprocessorParamsProvider.class)
	void processUnit(TestJavaFile testJavaFile) throws Exception {
		List<? extends Token> tokensMaster = testJavaFile.getTokens();
		List<? extends Token> tokens = (new CommentExtractor()).process(tokensMaster, null, "Java");
		Iterator<? extends Token> t = tokens.iterator();
		List<String> ls = new ArrayList<>();
		while (t.hasNext()) {
			ls.add(t.next().getText());
		}
		assertEquals(testJavaFile.CommentExtractor(), ls);
	}
}