package uk.ac.warwick.dcs.sherlock.api.model;

import java.util.*;
import java.util.stream.*;

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
	 * @param name         the reference identifier to given to the strategy
	 * @param preProcessor IPreprocessor instance(s) to be executed
	 *
	 * @return Generic strategy for the passed parameters
	 */
	@SafeVarargs
	static IPreProcessingStrategy of(String name, Class<? extends ITokenPreProcessor>... preProcessor) {
		return new GenericTokenPreProcessingStrategy(name, false, Arrays.asList(preProcessor));
	}

	/**
	 * A construction method for a generic preprocessing strategy
	 * <p><p>
	 * Output can be either formatted code or tokenised depending on the boolean flag value
	 *
	 * @param name         the reference identifier to given to the strategy
	 * @param tokenise     output will be tokenised
	 * @param preProcessor IPreprocessor instance(s) to be executed
	 *
	 * @return Generic strategy for the passed parameters
	 */
	@SafeVarargs
	static IPreProcessingStrategy of(String name, boolean tokenise, Class<? extends ITokenPreProcessor>... preProcessor) {
		return new GenericTokenPreProcessingStrategy(name, tokenise, Arrays.asList(preProcessor));
	}

	/**
	 * A construction method for a generic preprocessing strategy
	 *
	 * @param name         the reference identifier to given to the strategy
	 * @param preProcessor IPreprocessor instance(s) to be executed
	 *
	 * @return Generic strategy for the passed parameters
	 */
	static IPreProcessingStrategy of(String name, Class<? extends IParserPreProcessor> preProcessor) {
		return new GenericParserPreProcessingStrategy(name, preProcessor);
	}

	/**
	 * @return the reference identifier for the strategy
	 */
	String getName();

	/**
	 * @return ordered PreProcessor instance(s)
	 */
	List<Class<? extends IPreProcessor>> getPreProcessorClasses();

	/**
	 * @return stringifier to use before passing the file to the detection algorithm, null for the default formatted code
	 */
	default ITokenStringifier getStringifier() {
		return null;
	}

	/**
	 * Does the strategy use
	 *
	 * @return
	 */
	boolean isParserBased();

	/**
	 * Generic implementation of the IPreProcessingStrategy interface
	 */
	class GenericTokenPreProcessingStrategy implements IPreProcessingStrategy {

		private String name;
		private boolean tokenise;
		private List<Class<? extends IPreProcessor>> preProcessors;

		private GenericTokenPreProcessingStrategy(String name, boolean tokenise, List<Class<? extends ITokenPreProcessor>> preProcessors) {
			this.name = name;
			this.tokenise = tokenise;
			this.preProcessors = preProcessors.parallelStream().map(x -> (Class<? extends IPreProcessor>) x).collect(Collectors.toList());
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public List<Class<? extends IPreProcessor>> getPreProcessorClasses() {
			return this.preProcessors;
		}

		@Override
		public boolean isParserBased() {
			return false;
		}

		public boolean isResultTokenised() {
			return this.tokenise;
		}
	}

	class GenericParserPreProcessingStrategy implements IPreProcessingStrategy {

		private String name;
		private Class<? extends IParserPreProcessor> preProcessor;

		private GenericParserPreProcessingStrategy(String name, Class<? extends IParserPreProcessor> preProcessor) {
			this.name = name;
			this.preProcessor = preProcessor;
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public List<Class<? extends IPreProcessor>> getPreProcessorClasses() {
			return Collections.singletonList(this.preProcessor);
		}

		@Override
		public boolean isParserBased() {
			return true;
		}
	}

}
