package uk.ac.warwick.dcs.sherlock.model.base.preprocessing;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import uk.ac.warwick.dcs.sherlock.api.core.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.model.ITokeniser;

import java.util.LinkedList;
import java.util.List;

public class StandardTokeniser implements ITokeniser {

	/**
	 *  Tokenises a file in the form of a list of tokens
	 *
	 * @param tokens the file as a list of tokens
	 * @param vocab the lexer vocab
	 * @return indexed lines of the file, tokenised
	 */
	@Override
	public List<IndexedString> processTokens(List<Token> tokens, Vocabulary vocab) {
		List<IndexedString> output = new LinkedList<>();
		StringBuilder active = new StringBuilder(); //use string builder for much faster concatenation
		int lineCount = 1;

		for (Token t : tokens) {
			if (t.getLine() > lineCount) {
				if (active.length() > 0) {
					output.add(IndexedString.of(lineCount, active.toString()));
				}
				active.setLength(0);
				lineCount = t.getLine();
			}

			switch (StandardLexerSpecification.channels.values()[t.getChannel()]) {
				case DEFAULT:
					active.append(vocab.getSymbolicName(t.getType())).append(" ");
					break;
				default:
					break;
			}
		}

		if (active.length() > 0) {
			output.add(IndexedString.of(lineCount, active.toString()));
		}

		return output;
	}
}
