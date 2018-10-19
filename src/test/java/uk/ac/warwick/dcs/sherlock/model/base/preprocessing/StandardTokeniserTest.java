package uk.ac.warwick.dcs.sherlock.model.base.preprocessing;

import org.antlr.v4.runtime.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import uk.ac.warwick.dcs.sherlock.api.util.IndexedString;
import uk.ac.warwick.dcs.sherlock.model.base.lang.JavaLexer;
import uk.ac.warwick.dcs.sherlock.model.base.utils.PreprocessorParamsProvider;
import uk.ac.warwick.dcs.sherlock.model.base.utils.TestJavaFile;
import uk.ac.warwick.dcs.sherlock.model.base.utils.TestUtils;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StandardTokeniserTest {

	@ParameterizedTest
	@ArgumentsSource (PreprocessorParamsProvider.class)
	void processTokens(TestJavaFile testJavaFile) throws Exception {
		File file = TestUtils.makeFileWithContents("temp.java", testJavaFile.Original());
		Class<? extends Lexer> lexerClass = JavaLexer.class;
		Lexer lexer = lexerClass.getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromFileName(file.getName()));
		List<? extends Token> tokensMaster = lexer.getAllTokens();
		List<IndexedString> indexedStrings = (new StandardTokeniser()).processTokens(tokensMaster, lexer.getVocabulary());
		Iterator<IndexedString> i = indexedStrings.iterator();
		List<String> ls = new ArrayList<>();
		while (i.hasNext()) {
			ls.add(i.next().getValue());
		}
		FileUtils.forceDelete(file);
		assertEquals(testJavaFile.StandardTokeniser(), ls);
	}
}