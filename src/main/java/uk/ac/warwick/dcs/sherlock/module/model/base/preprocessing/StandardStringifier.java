package uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.ITokenStringifier;
import uk.ac.warwick.dcs.sherlock.api.common.IndexedString;

import java.util.*;

public class StandardStringifier implements ITokenStringifier {

	/**
	 * Stringifies a file in the form of a list of tokens
	 *
	 * @param tokens the file as a list of tokens
	 * @param vocab  the lexer vocab
	 *
	 * @return indexed lines of the file, stringified
	 */
	@Override
	public List<IndexedString> processTokens(List<? extends Token> tokens, Vocabulary vocab) {
		List<IndexedString> output = new LinkedList<>();
		StringBuilder active = new StringBuilder(); //use string builder for much faster concatenation
		int lineCount = 1;

		for (Token t : tokens) {
			lineCount = checkLineFinished(output, active, lineCount, t);

			switch (StandardLexerSpecification.channels.values()[t.getChannel()]) {
				case COMMENT:
					lineCount = preserveCommentLines(output, active, lineCount, t);
					break;
				case DEFAULT:
				case WHITESPACE:
				case LONG_WHITESPACE:
					active.append(t.getText());
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

	/**
	 * Method to check if a line has been finished, if it has it adds the line to the output and moves onto the next
	 *
	 * @param output    list of output strings
	 * @param active    current string
	 * @param lineCount current lineCount
	 * @param t         next token
	 *
	 * @return new lineCount
	 */
	public static int checkLineFinished(List<IndexedString> output, StringBuilder active, int lineCount, Token t) {
		if (t.getLine() > lineCount) {
			if (active.length() > 0) {
				output.add(IndexedString.of(lineCount, active.toString()));
			}
			active.setLength(0);
			lineCount = t.getLine();
		}

		return lineCount;
	}

	/**
	 * Method to split a comment out into its component lines if it spans more than one line to preserve the sourcefile line structure
	 *
	 * @param output    list of output strings
	 * @param active    current string
	 * @param lineCount current lineCount
	 * @param t         token containing comment
	 *
	 * @return new lineCount
	 */
	public static int preserveCommentLines(List<IndexedString> output, StringBuilder active, int lineCount, Token t) {
		String[] splitComment = t.getText().split("\\r?\\n|\\r");

		for (int i = 0; i < splitComment.length; i++) {
			if (i != 0) {
				output.add(IndexedString.of(lineCount, active.toString()));
				active.setLength(0);
				lineCount++;
			}
			active.append(splitComment[i].trim());
		}
		return lineCount;
	}
}
