package uk.ac.warwick.dcs.sherlock.model.base.preprocessing;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import uk.ac.warwick.dcs.sherlock.api.model.ILexerSpecification;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessorBase;

import java.util.stream.Stream;

public class CommentExtractor implements IPreProcessorBase {

	@Override
	public ILexerSpecification getLexerSpecification() {
		return new StandardLexerSpecification();
	}

	/**
	 * Extracts the comments from a sourcefile
	 *
	 * @param lexer input of lexer instance containing the unprocessed lines
	 *
	 * @return stream of comments, 1 per string
	 */
	@Override
	public Stream<String> process(Lexer lexer) {
		Stream.Builder<String> builder = Stream.builder();
		StringBuilder active = new StringBuilder();
		int lineCount = 1;

		for (Token t : lexer.getAllTokens()) {
			while (t.getLine() > lineCount) {
				builder.add(active.toString());
				active.setLength(0);
				lineCount++;
			}

			switch (StandardLexerSpecification.channels.values()[t.getChannel()]) {
				case COMMENT:
					lineCount = preserveCommentLines(builder, active, lineCount, t);
					break;
				default:
					break;
			}
		}
		builder.add(active.toString());

		return builder.build();
	}

	/**
	 * Method to split a comment out into its component lines if it spans more than one line to preserve the sourcefile line structure
	 *
	 * @param builder   stream builder instance in use
	 * @param active    current string
	 * @param lineCount current lineCount
	 * @param t         token containing comment
	 *
	 * @return new lineCount
	 */
	public static int preserveCommentLines(Stream.Builder<String> builder, StringBuilder active, int lineCount, Token t) {
		String[] splitComment = t.getText().split("\\r?\\n|\\r");

		for (int i = 0; i < splitComment.length; i++) {
			if (i != 0) {
				builder.add(active.toString());
				active.setLength(0);
				lineCount++;
			}
			active.append(splitComment[i].trim());
		}
		return lineCount;
	}

}
