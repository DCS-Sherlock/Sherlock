package uk.ac.warwick.dcs.sherlock.model.base.preprocessing.processors;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import uk.ac.warwick.dcs.sherlock.api.model.ILexerSpecification;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.Language;
import uk.ac.warwick.dcs.sherlock.model.base.preprocessing.StandardLexer;

import java.util.stream.Stream;

public class CommentRemover implements IPreProcessor {

	@Override
	public Class<? extends ILexerSpecification> getLexerSpecification() {
		return StandardLexer.class;
	}

	/**
	 * Preprocessor to remove comments and trim whitespace from source
	 *
	 * @param lexer input of lexer instance containing the unprocessed lines
	 * @param lang reference of the language of the lexer
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

			switch (StandardLexer.channels.values()[t.getChannel()]) {
				case DEFAULT: case WHITESPACE:
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
