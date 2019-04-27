package uk.ac.warwick.dcs.sherlock.api.model.preprocessing;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.util.IndexedString;

import java.util.*;

/**
 * Interface to define the final step in file preprocessing, it turns the list of tokens into a list of strings paired with their respective line numbers.
 * <p>
 * Standard implementations include formatted code (output in same format as input file) and tokenising.
 */
public interface ITokenStringifier {

	/**
	 * Transform list of tokens into a list of indexed strings, where each string is a line of the original file, and the index of the string is the line number
	 *
	 * @param tokens the list of preprocessed tokens
	 * @param vocab  lexer vocabulary (for tokenising)
	 *
	 * @return list of line number indexed strings
	 */
	List<IndexedString> processTokens(List<? extends Token> tokens, Vocabulary vocab);

}
