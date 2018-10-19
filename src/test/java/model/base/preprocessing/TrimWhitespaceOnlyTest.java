package model.base.preprocessing;

import model.base.utils.PreprocessorParamsProvider;
import model.base.utils.TestJavaFile;
import model.base.utils.TestUtils;
import org.antlr.v4.runtime.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import uk.ac.warwick.dcs.sherlock.api.model.*;
import uk.ac.warwick.dcs.sherlock.model.base.preprocessing.*;
import uk.ac.warwick.dcs.sherlock.model.base.lang.JavaLexer;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TrimWhitespaceOnlyTest {

    @Test
    void getLexerSpecification() {
        ILexerSpecification ls = (new TrimWhitespaceOnly()).getLexerSpecification();
        assertTrue(ls instanceof StandardLexerSpecification);
    }

    @ParameterizedTest
    @ArgumentsSource(PreprocessorParamsProvider.class)
    void process(TestJavaFile testJavaFile) throws Exception {
        File file = TestUtils.makeFileWithContents("temp.java", testJavaFile.Original());
        Class<? extends Lexer> lexerClass = JavaLexer.class;
        Lexer lexer = lexerClass.getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromFileName(file.getName()));
        List<? extends Token> tokensMaster = lexer.getAllTokens();
        List<? extends Token> tokens = (new TrimWhitespaceOnly()).process(tokensMaster, lexer.getVocabulary(), Language.JAVA);
        Iterator<? extends  Token> t = tokens.iterator();
        List<String> ls = new ArrayList<>();
        while(t.hasNext()){
            ls.add(t.next().getText());
        }
        FileUtils.forceDelete(file);
        assertEquals(testJavaFile.TrimWhiteSpaceOnly(), ls);
    }

    @ParameterizedTest
    @ArgumentsSource(PreprocessorParamsProvider.class)
    void processUnit(TestJavaFile testJavaFile) throws Exception {
        List<? extends Token> tokensMaster = testJavaFile.getTokens();
        List<? extends Token> tokens = (new TrimWhitespaceOnly()).process(tokensMaster, null, Language.JAVA);
        Iterator<? extends  Token> t = tokens.iterator();
        List<String> ls = new ArrayList<>();
        while(t.hasNext()){
            ls.add(t.next().getText());
        }
        assertEquals(testJavaFile.TrimWhiteSpaceOnly(), ls);
    }
}
