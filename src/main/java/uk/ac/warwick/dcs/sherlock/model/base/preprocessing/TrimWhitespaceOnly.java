package uk.ac.warwick.dcs.sherlock.model.base.preprocessing;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import uk.ac.warwick.dcs.sherlock.api.model.ILexerSpecification;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.Language;

import java.util.ArrayList;
import java.util.List;

public class TrimWhitespaceOnly implements IPreProcessor {

	@Override
	public ILexerSpecification getLexerSpecification() {
		return new StandardLexerSpecification();
	}

	/**
	 * Removes the excess whitespace from a sourcefile
	 *
	 * @param tokens List of tokens to process
	 * @param vocab Lexer vocabulary
	 * @param lang language of source file being processed
	 *
	 * @return stream of tokens containing comments
	 */
	@Override
	public List<? extends Token> process(List<? extends Token> tokens, Vocabulary vocab, Language lang) {
		List<Token> result =  new ArrayList<>();

		for (Token t : tokens) {

			switch (StandardLexerSpecification.channels.values()[t.getChannel()]) {
				case COMMENT:
					result.add(t);
					break;
				case DEFAULT:
					result.add(t);
					break;
				case WHITESPACE:
					result.add(t);
					break;
				default:
					break;
			}
		}

		return result;
	}
}
