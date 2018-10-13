package uk.ac.warwick.dcs.sherlock.model.base.preprocessing;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.Language;
import uk.ac.warwick.dcs.sherlock.model.base.StandardLexer;

import java.util.stream.Stream;

public class TestPreProcessor implements IPreProcessor {

	/**
	 * Example preprocessor - does absolutely nothing xD
	 *
	 * @param lexer input of lexer instance containing the unprocessed lines
	 * @param lang reference of the language of the lexer
	 * @return stream of processed lines, 1 line per string
	 */
	@Override
	public Stream<String> process(Lexer lexer, Language lang) {
		Stream.Builder<String> builder = Stream.builder();
		StringBuilder active = new StringBuilder(); //use string builder for much faster concatenation

		for (Token t : lexer.getAllTokens()) {
			switch (StandardLexer.channels.values()[t.getChannel()]) {
				case LINE_ENDING:
					builder.add(active.toString());
					active.setLength(0); //clear the string builder
					break;
				default:
					active.append(t.getText());
			}
		}

		return builder.build();
	}
}
