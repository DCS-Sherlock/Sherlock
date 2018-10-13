package uk.ac.warwick.dcs.sherlock.api.model;

import java.util.stream.Stream;

public interface IPreProcessingStrategy {

	/**
	 * Define a set of preprocessors, to be run, in order, on each file
	 *
	 * @return a  stream containing the set of preprocessors
	 */
	Stream<Class<? extends IPreProcessor>> definePreProcessingStrategy();

}
