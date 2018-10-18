package model.base.utils;

import org.antlr.v4.runtime.Token;

import java.util.List;

public interface TestJavaFile {
    String Original();
    List<Token> getTokens();
    List<String> TrimWhiteSpaceOnly();
    List<String> CommentRemover();
    List<String> CommentExtractor();
    List<String> StandardStringifier();
    List<String> StandardTokeniser();
}
