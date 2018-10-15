package uk.ac.warwick.dcs.sherlock.api.model;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;

import java.util.List;

public interface IPreProcessorFilter extends IPreProcessorBase<Token> {

	/**
	 * Method to perform preprocessing filtering on a source file.
	 *
	 * @param tokens List of tokens to process
	 * @param vocab  Lexer vocabulary
	 * @param lang   language of source file being processed
	 *
	 * @return output list of filtered tokens
	 */
	@Override
	List<Token> process(List<Token> tokens, Vocabulary vocab, Language lang);
}
