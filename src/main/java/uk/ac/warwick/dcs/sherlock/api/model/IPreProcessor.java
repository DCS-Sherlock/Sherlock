package uk.ac.warwick.dcs.sherlock.api.model;

import java.util.stream.Stream;

public interface IPreProcessor {

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
	Stream<String> process(Stream<String> input);

}
