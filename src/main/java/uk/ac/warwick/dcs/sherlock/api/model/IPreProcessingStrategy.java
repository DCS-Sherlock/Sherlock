package uk.ac.warwick.dcs.sherlock.api.model;

import java.util.*;

/**
 * A construct to specify a named set of preprocessing steps.
 * <p><p>
 * The preprocessors are executed in list order. The Stringifier can be set to allow for special output processing or tokenising.
 */
public interface IPreProcessingStrategy {

	/**
	 * A construction method for a generic preprocessing strategy
	 * <p><p>
	 * Output is always normal formatted code
	 *
 	 * @param name the reference identifier to given to the strategy
	 * @param preProcessor IPreprocessor instance(s) to be executed
	 * @return Generic strategy for the passed parameters
	 */
	@SafeVarargs
	static IPreProcessingStrategy of(String name, Class<? extends IPreProcessor>... preProcessor) {
		return new GenericPreProcessingStrategy(name, false, Arrays.asList(preProcessor));
	}

	/**
	 * A construction method for a generic preprocessing strategy
	 * <p><p>
	 * Output can be either formatted code or tokenised depending on the boolean flag value
	 *
	 * @param name the reference identifier to given to the strategy
	 * @param tokenise output will be tokenised
	 * @param preProcessor IPreprocessor instance(s) to be executed
	 * @return Generic strategy for the passed parameters
	 */
	@SafeVarargs
	static IPreProcessingStrategy of(String name, boolean tokenise, Class<? extends IPreProcessor>... preProcessor) {
		return new GenericPreProcessingStrategy(name, tokenise, Arrays.asList(preProcessor));
	}

	/**
	 * @return the reference identifier for the strategy
	 */
	String getName();

	/**
	 * @return ordered IPreprocessor instance(s)
	 */
	List<Class<? extends IPreProcessor>> getPreProcessorClasses();

	/**
	 * @return stringifier to use before passing the file to the detection algorithm, null for the default formatted code
	 */
	default ITokenStringifier getStringifier() {
		return null;
	}

	/**
	 * Generic implementation of the IPreProcessingStrategy interface
	 */
	class GenericPreProcessingStrategy implements IPreProcessingStrategy {

		private String name;
		private boolean tokenise;
		private List<Class<? extends IPreProcessor>> preProcessors;

		private GenericPreProcessingStrategy(String name, boolean tokenise, List<Class<? extends IPreProcessor>> preProcessors) {
			this.name = name;
			this.tokenise = tokenise;
			this.preProcessors = preProcessors;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public List<Class<? extends IPreProcessor>> getPreProcessorClasses() {
			return this.preProcessors;
		}

		public boolean isResultTokenised() {
			return this.tokenise;
		}
	}

}
