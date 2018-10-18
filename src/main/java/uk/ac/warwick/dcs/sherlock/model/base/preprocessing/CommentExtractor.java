package uk.ac.warwick.dcs.sherlock.model.base.preprocessing;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import uk.ac.warwick.dcs.sherlock.api.model.ILexerSpecification;
import uk.ac.warwick.dcs.sherlock.api.model.IPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.Language;

import java.util.*;

public class CommentExtractor implements IPreProcessor {

	@Override
	public ILexerSpecification getLexerSpecification() {
		return new StandardLexerSpecification();
	}

	/**
	 * Extracts the comments from a source file
	 * <p>
	 * Parallel version: return tokens.parallelStream().filter(t -> StandardLexerSpecification.channels.values()[t.getChannel()] == StandardLexerSpecification.channels.COMMENT).collect(Collectors.toList());
	 *
	 * @param tokens list of tokens to process
	 * @param vocab  Lexer vocabulary
	 * @param lang   language of source file being processed
	 *
	 * @return stream of tokens containing comments
	 */
	@Override
	public List<? extends Token> process(List<? extends Token> tokens, Vocabulary vocab, Language lang) {
		List<Token> result = new ArrayList<>();

		for (Token t : tokens) {

			switch (StandardLexerSpecification.channels.values()[t.getChannel()]) {
				case COMMENT:
					result.add(t);
					break;
				default:
					break;
			}
		}

		return result;
	}

}
