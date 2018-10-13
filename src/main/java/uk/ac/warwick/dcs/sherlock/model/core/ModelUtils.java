package uk.ac.warwick.dcs.sherlock.model.core;

import org.antlr.v4.runtime.Lexer;
import uk.ac.warwick.dcs.sherlock.api.model.ILexerSpecification;

public class ModelUtils {

	/**
	 * Checks a lexer conforms to the specification
	 * @param lexer lexer instance to check
	 * @param specification specification to check against
	 * @return does it conform?
	 */
	public static boolean checkLexerAgainstSpecification(Lexer lexer, ILexerSpecification specification) {

		if (lexer.getChannelNames().length < specification.getChannelNames().length) return false;

		for (int i = 0; i < specification.getChannelNames().length; i++) {
			if (!lexer.getChannelNames()[i].equals(specification.getChannelNames()[i]) && !specification.getChannelNames().equals("-")) return false;
		}

		return true;
	}

}
