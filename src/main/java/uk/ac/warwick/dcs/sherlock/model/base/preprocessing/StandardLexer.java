package uk.ac.warwick.dcs.sherlock.model.base.preprocessing;

import uk.ac.warwick.dcs.sherlock.api.model.ILexerSpecification;

/**
 * Provides a specification for all language lexers to conform to -- make this an api and allow for custom definitions, still with O(n) recall
 */
public class StandardLexer implements ILexerSpecification {

	private static String[] channelNames = { "DEFAULT_TOKEN_CHANNEL", "HIDDEN", "LINE_ENDING", "WHITESPACE", "LONG_WHITESPACE", "COMMENT" };

	@Override
	public String[] getChannelNames() {
		return channelNames;
	}

	/**
	 * reference enum
	 */
	public enum channels {
		DEFAULT, HIDDEN, LINE_ENDING, WHITESPACE, LONG_WHITESPACE, COMMENT
	}

}
