package uk.ac.warwick.dcs.sherlock.model.base.preprocessing;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import uk.ac.warwick.dcs.sherlock.api.model.ILexerSpecification;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.Language;

import java.util.stream.Stream;

public class TrimWhitespaceOnly implements IPreProcessor {

	@Override
	public Class<? extends ILexerSpecification> getLexerSpecification() {
		return StandardLexerSpecification.class;
	}

	/**
	 * Removes the excess whitespace from a sourcefile
	 *
	 * @param lexer input of lexer instance containing the unprocessed lines
	 * @param lang  reference of the language of the lexer
	 *
	 * @return stream of processed lines, 1 line per string
	 */
	@Override
	public Stream<String> process(Lexer lexer, Language lang) {
		Stream.Builder<String> builder = Stream.builder();
		StringBuilder active = new StringBuilder(); //use string builder for much faster concatenation
		int lineCount = 1;

		for (Token t : lexer.getAllTokens()) {
			while (t.getLine() > lineCount) {
				builder.add(active.toString());
				active.setLength(0);
				lineCount++;
			}

			switch (StandardLexerSpecification.channels.values()[t.getChannel()]) {
				case COMMENT:
					lineCount = CommentExtractor.preserveCommentLines(builder, active, lineCount, t);
					break;
				case DEFAULT:
				case WHITESPACE:
					active.append(t.getText());
					break;
				default:
					break;
			}
		}
		builder.add(active.toString());

		return builder.build();
	}
}
