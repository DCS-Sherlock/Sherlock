package uk.ac.warwick.dcs.sherlock.model.base.preprocessing.processors;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import uk.ac.warwick.dcs.sherlock.api.model.ILexerSpecification;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.Language;
import uk.ac.warwick.dcs.sherlock.model.base.preprocessing.StandardLexer;

import java.util.stream.Stream;

public class CommentExtractor implements IPreProcessor {

	@Override
	public Class<? extends ILexerSpecification> getLexerSpecification() {
		return StandardLexer.class;
	}

	/**
	 * Extracts the comments in a sourcefile
	 *
	 * @param lexer input of lexer instance containing the unprocessed lines
	 * @param lang  reference of the language of the lexer
	 *
	 * @return stream of comments, 1 per string
	 */
	@Override
	public Stream<String> process(Lexer lexer, Language lang) {
		Stream.Builder<String> builder = Stream.builder();

		for (Token t : lexer.getAllTokens()) {
			switch (StandardLexer.channels.values()[t.getChannel()]) {
				case COMMENT:
					builder.add(t.toString());
					break;
				default:
					break;
			}
		}

		return builder.build();
	}

}
