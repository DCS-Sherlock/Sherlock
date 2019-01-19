package uk.ac.warwick.dcs.sherlock.api.model.preprocessing;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.common.IndexedString;

import java.util.*;

public interface IAdvancedPreProcessor<T extends Lexer> {

	/**
	 * Pre-process with a lexer
	 *
	 * @param lexer lexer instance
	 *
	 * @return
	 */
	List<IndexedString> process(T lexer);

}
