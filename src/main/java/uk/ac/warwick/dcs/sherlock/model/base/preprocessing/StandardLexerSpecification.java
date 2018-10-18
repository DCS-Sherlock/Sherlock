package uk.ac.warwick.dcs.sherlock.model.base.preprocessing;

import uk.ac.warwick.dcs.sherlock.api.model.ILexerSpecification;

/**
 * Provides a specification for a basic lexer for preprocessing
 */
public class StandardLexerSpecification implements ILexerSpecification {

	private static String[] channelNames = { "DEFAULT_TOKEN_CHANNEL", "COMMENT", "WHITESPACE", "LONG_WHITESPACE", "HIDDEN"  };

	@Override
	public String[] getChannelNames() {
		return channelNames;
	}

	/**
	 * reference enum
	 */
	public enum channels {
		DEFAULT, COMMENT, WHITESPACE, LONG_WHITESPACE, HIDDEN
	}

}
