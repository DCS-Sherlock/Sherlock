package uk.ac.warwick.dcs.sherlock.api.model;

import org.antlr.v4.runtime.Lexer;

import java.util.stream.Stream;

public interface IPreProcessor {

	/**
	 * UNUSED - for future optimisations
	 *
	 * Should the result of this preprocessor be cached?
	 */
	default boolean cacheResult() {
		return true;
	}

	/**
	 * Method to perform preprocessing on a line by line stream of a file, this may have been processed by other preprocessors already.
	 *
	 * Use Stream.builder to construct the output stream
	 *
	 * Example:
	 *      Stream.Builder<String> output = Stream.builder();
	 *      input.forEach(output:add);
	 *      return output
	 *
	 * @param input input stream of unprocessed lines
	 * @return output stream of processed lines
	 */
	Stream<String> process(Lexer lexer, Language lang);

}
