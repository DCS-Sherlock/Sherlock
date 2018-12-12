package uk.ac.warwick.dcs.sherlock.api.model;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.util.IndexedString;

import java.util.*;

public interface IParserPreProcessor extends IPreProcessor {

	/**
	 * Pre-process with a parser
	 * @param lexer lexer instance
	 * @param lang
	 * @return
	 */
	List<IndexedString> processTokens(Lexer lexer, Class<? extends Parser> parserClass, Language lang);

	/**
	 * Returns the parser used for each language, please ensure this is correct
	 * @param lang
	 * @return
	 */
	Class<? extends Parser> getParserUsed(Language lang);

}