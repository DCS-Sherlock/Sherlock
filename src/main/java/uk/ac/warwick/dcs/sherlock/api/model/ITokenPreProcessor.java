package uk.ac.warwick.dcs.sherlock.api.model;

import org.antlr.v4.runtime.*;

import java.util.*;

public interface ITokenPreProcessor extends IPreProcessor {

	/**
	 * Specify a set of dependencies which must be present in and executed prior to this preprocessor in a {@link IPreProcessingStrategy}
	 *
	 * @return list of dependencies, ordered.
	 */
	default List<Class<? extends ITokenPreProcessor>> getDependencies() {
		return null;
	}

	/**
	 * Specifies which channels must be present in the lexer for the preprocessor to function, this is a minimum specification, other channels may be present.
	 *
	 * @return lexer specification
	 */
	ILexerSpecification getLexerSpecification();

	/**
	 * Method to perform preprocessing filtering on a source file.
	 *
	 * @param tokens List of tokens to process
	 * @param vocab  Lexer vocabulary
	 * @param lang   language of source file being processed
	 *
	 * @return output list of filtered tokens
	 */
	List<? extends Token> process(List<? extends Token> tokens, Vocabulary vocab, Language lang);

}
