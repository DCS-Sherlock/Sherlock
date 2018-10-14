package uk.ac.warwick.dcs.sherlock.api.model;

import org.antlr.v4.runtime.Lexer;

import java.util.stream.Stream;

public interface IPreProcessor {

	/**
	 * Specifies which channels must be present in the lexer for the preprocessor to function, this is a minimum specification, other channels may be present.
	 *
	 * @return LexerSpecification
	 */
	ILexerSpecification getLexerSpecification();

	/**
	 * Method to perform preprocessing on a line by line stream of a file, this may have been processed by other preprocessors already.
	 * <p>
	 * Use Stream.builder to construct the output stream
	 * <p>
	 * Example: Stream.Builder<String> output = Stream.builder(); input.forEach(output:add); return output
	 *
	 * @param lexer input of lexer instance containing the unprocessed lines
	 *
	 * @return output stream of processed lines, 1 String per line
	 */
	Stream<String> process(Lexer lexer);

}
