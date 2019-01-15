package uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing;

import org.antlr.v4.runtime.*;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.ILexerSpecification;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.ITokenPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;

import java.util.*;

public class CommentExtractor implements ITokenPreProcessor {

	@Override
	public ILexerSpecification getLexerSpecification() {
		return new StandardLexerSpecification();
	}

	/**
	 * Extracts the comments from a source file
	 *
	 * @param tokens list of tokens to process
	 * @param vocab  Lexer vocabulary
	 * @param lang   language of source file being processed
	 *
	 * @return stream of tokens containing comments
	 */
	@Override
	public List<? extends Token> process(List<? extends Token> tokens, Vocabulary vocab, Language lang) {
		//Parallel version: return tokens.parallelStream().filter(t -> StandardLexerSpecification.channels.values()[t.getChannel()] == StandardLexerSpecification.channels.COMMENT).collect(Collectors.toList());

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
