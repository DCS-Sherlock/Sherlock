package uk.ac.warwick.dcs.sherlock.model.base.preprocessing;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import uk.ac.warwick.dcs.sherlock.api.model.ILexerSpecification;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessor;

import java.util.stream.Stream;

public class SourceTokeniser implements IPreProcessor {

	@Override
	public ILexerSpecification getLexerSpecification() {
		return new StandardLexerSpecification();
	}

	/**
	 * Tokenises a sourcefile
	 *
	 * @param lexer input of lexer instance containing the unprocessed lines
	 *
	 * @return stream of tokenised source, 1 line per string
	 */
	@Override
	public Stream<String> process(Lexer lexer) {
		Vocabulary vocab = lexer.getVocabulary();
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
				case DEFAULT:
					active.append(vocab.getSymbolicName(t.getType())).append(" ");
					break;
				default:
					break;
			}
		}
		builder.add(active.toString());

		return builder.build();
	}

}
