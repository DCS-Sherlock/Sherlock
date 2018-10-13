package uk.ac.warwick.dcs.sherlock.model.base;

import uk.ac.warwick.dcs.sherlock.api.model.ILexerSpecification;

/**
 * Provides a specification for all language lexers to conform to -- make this an api and allow for custom definitions, still with O(n) recall
 */
public class StandardLexer implements ILexerSpecification {

	private static String[] channelNames = { "DEFAULT_TOKEN_CHANNEL", "HIDDEN", "LINE_ENDING", "WHITESPACE", "LONG_WHITESPACE", "COMMENT" };


	public String[] getChannelNames() {
		return channelNames;
	}

	/**
	 * Checks a lexer conforms to the specification
	 * @param lexer lexer instance to check
	 * @return does it conform?
	 */
	/*public boolean checkLexer(Lexer lexer) {

		if (lexer.getChannelNames().length < channelNames.length) return false;

		for (int i = 0; i < channelNames.length; i++) {
			if (!lexer.getChannelNames()[i].equals(channelNames[i])) return false;
		}

		return true;
	}*/

	/**
	 * reference enum
	 */
	public enum channels {
		DEFAULT, HIDDEN, LINE_ENDING, WHITESPACE, LONG_WHITESPACE, COMMENT
	}

}
