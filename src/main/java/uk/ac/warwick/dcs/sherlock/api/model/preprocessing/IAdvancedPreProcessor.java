package uk.ac.warwick.dcs.sherlock.api.model.preprocessing;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.util.IndexedString;

import java.util.*;

/**
 * Advanced preprocessor implementation, used to directly access and preprocess from a specific lexer
 * @param <T> Antlr lexer implementation (compiled)
 */
public interface IAdvancedPreProcessor<T extends Lexer> {

	/**
	 * Pre-process with a lexer
	 *
	 * @param lexer lexer instance
	 *
	 * @return list of processed strings, indexed by line number
	 */
	List<IndexedString> process(T lexer);

}
