package uk.ac.warwick.dcs.sherlock.api.model;

import java.util.stream.Stream;

public interface IPreProcessingStrategy {

	/**
	 * Define a set of preprocessors, to be run, in order, on each file
	 *
	 * Example: return Stream.of(preproc1.class, proproc2.class, ....);
	 *
	 * @return a  stream containing the set of preprocessors
	 */
	Stream<Class<? extends IPreProcessor>> definePreProcessingStrategy();

	/**
	 * Returns the appropriate Tokeniser for this strategy and the language of the source files
	 *
	 * @param lang the language of the source files
	 * @return the ITokeniser class to use as the final preprocessing step
	 */
	default Class<? extends ITokeniser> getTokeniser(Language lang) {
		switch (lang) {
			case JAVA:
				return null;
			default:
				return null;
		}
	}

}
