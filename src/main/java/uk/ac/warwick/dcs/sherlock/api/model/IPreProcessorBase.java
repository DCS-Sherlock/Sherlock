package uk.ac.warwick.dcs.sherlock.api.model;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;

import java.util.List;

/**
 * Generic IPreProcessor interface, do not implement directly, instead use either IPreProcessorFilter or IPreProcessorMapper
 * @param <E> return type of the preprocessor
 */
public interface IPreProcessorBase<E> {

	/**
	 * Specifies which channels must be present in the lexer for the preprocessor to function, this is a minimum specification, other channels may be present.
	 *
	 * @return LexerSpecification
	 */
	ILexerSpecification getLexerSpecification();

	/**
	 * Method to perform preprocessing
	 *
	 * @param tokens List of tokens to process
     * @param vocab Lexer vocabulary
	 * @param lang language of source file being processed
	 *
	 * @return output list of return items
	 */
	List<E> process(List<Token> tokens, Vocabulary vocab, Language lang);

}
