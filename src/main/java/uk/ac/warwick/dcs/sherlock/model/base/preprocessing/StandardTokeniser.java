package uk.ac.warwick.dcs.sherlock.model.base.preprocessing;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import uk.ac.warwick.dcs.sherlock.api.core.IndexedString;
import uk.ac.warwick.dcs.sherlock.api.model.ITokenStringifier;

import java.util.LinkedList;
import java.util.List;

public class StandardTokeniser implements ITokenStringifier {

	/**
	 *  Tokenises a file in the form of a list of tokens
	 *
	 * @param tokens the file as a list of tokens
	 * @param vocab the lexer vocab
	 * @return indexed lines of the file, tokenised
	 */
	@Override
	public List<IndexedString> processTokens(List<? extends Token> tokens, Vocabulary vocab) {
		List<IndexedString> output = new LinkedList<>();
		StringBuilder active = new StringBuilder(); //use string builder for much faster concatenation
		int lineCount = 1;

		for (Token t : tokens) {
			lineCount = StandardStringifier.checkLineFinished(output, active, lineCount, t);

			switch (StandardLexerSpecification.channels.values()[t.getChannel()]) {
				case DEFAULT:
					active.append(vocab.getSymbolicName(t.getType())).append(" ");
					break;
				case COMMENT:
					lineCount = preserveCommentLines(output, active, lineCount, t, vocab);
					break;
				default:
					break;
			}
		}

		if (active.length() > 0) {
			output.add(IndexedString.of(lineCount, active.toString().trim()));
		}

		return output;
	}

	public static int preserveCommentLines(List<IndexedString> output, StringBuilder active, int lineCount, Token t, Vocabulary vocab) {
		String[] splitComment = t.getText().split("\\r?\\n|\\r");

		for (int i = 0; i < splitComment.length; i++) {
			if (i != 0) {
				output.add(IndexedString.of(lineCount, active.toString()));
				active.setLength(0);
				lineCount++;
			}
		}
		active.append(vocab.getSymbolicName(t.getType()));
		return lineCount;
	}
}
