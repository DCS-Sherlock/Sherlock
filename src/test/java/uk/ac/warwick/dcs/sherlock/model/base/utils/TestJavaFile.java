package uk.ac.warwick.dcs.sherlock.model.base.utils;

import org.antlr.v4.runtime.*;

import java.util.*;

public interface TestJavaFile {

	List<String> CommentExtractor();

	List<String> CommentRemover();

	String Original();

	List<String> StandardStringifier();

	List<String> StandardTokeniser();

	List<String> TrimWhiteSpaceOnly();

	List<Token> getTokens();
}
