package uk.ac.warwick.dcs.sherlock.model.base.preprocessing;

import uk.ac.warwick.dcs.sherlock.api.model.ILexerSpecification;

/**
 * Provides a specification for a basic lexer for preprocessing
 */
public class StandardLexerSpecification implements ILexerSpecification {

	private static String[] channelNames = { "DEFAULT_TOKEN_CHANNEL", "HIDDEN", "WHITESPACE", "LONG_WHITESPACE", "COMMENT" };

	@Override
	public String[] getChannelNames() {
		return channelNames;
	}

	/**
	 * reference enum
	 */
	public enum channels {
		DEFAULT, HIDDEN, WHITESPACE, LONG_WHITESPACE, COMMENT
	}

}
