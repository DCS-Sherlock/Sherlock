package uk.ac.warwick.dcs.sherlock.api.model;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import uk.ac.warwick.dcs.sherlock.api.core.IndexedString;

import java.util.List;

public interface IPreProcessorMapper extends IPreProcessorBase<IndexedString> {

	/**
	 * Method to perform preprocessing mapping on a source file, from tokens to strings. Useful for tokenising a file.
	 *
	 * @param tokens List of tokens to process
	 * @param vocab  Lexer vocabulary
	 * @param lang   language of source file being processed
	 *
	 * @return output list of indexed strings from the source file
	 */
	@Override
	List<IndexedString> process(List<Token> tokens, Vocabulary vocab, Language lang);
}
