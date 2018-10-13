package uk.ac.warwick.dcs.sherlock.api.model;

import org.antlr.v4.runtime.Lexer;

/**
 * Provides a specification for all language lexers to conform to
 */
public class LexerSpecification {

	private static String[] channelNames = { "DEFAULT_TOKEN_CHANNEL", "HIDDEN", "LINE_ENDING", "WHITESPACE", "LONG_WHITESPACE", "COMMENT" };

	/**
	 * Checks a lexer conforms to the specification
	 * @param lexer lexer instance to check
	 * @return does it conform?
	 */
	public boolean checkLexer(Lexer lexer) {

		if (lexer.getChannelNames().length != channelNames.length) return false;

		for (int i = 0; i < channelNames.length; i++) {
			if (!lexer.getChannelNames()[i].equals(channelNames[i])) return false;
		}

		return true;
	}

	/**
	 * Channel reference enum
	 */
	public enum channels {
		DEFAULT, HIDDEN, LINE_ENDING, WHITESPACE, LONG_WHITESPACE, COMMENT
	}

}
