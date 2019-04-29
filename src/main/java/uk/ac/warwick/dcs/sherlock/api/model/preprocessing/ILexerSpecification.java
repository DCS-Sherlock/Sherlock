package uk.ac.warwick.dcs.sherlock.api.model.preprocessing;

/**
 * Defines a list of channel names, used to identify and catagorise tokens, specified in the lexer.
 */
public interface ILexerSpecification {

	/**
	 * @return a list of channel names, used to identify and catagorise tokens, specified in the lexer. Must be in order of the declaration in the lexer
	 */
	String[] getChannelNames();

}
