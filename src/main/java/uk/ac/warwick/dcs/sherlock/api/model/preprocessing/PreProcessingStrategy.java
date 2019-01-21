package uk.ac.warwick.dcs.sherlock.api.model.preprocessing;

import java.util.*;
import java.util.stream.*;

/**
 * A construct to specify a named set of preprocessing steps.
 * <br><br>
 * The preprocessors are executed in list order. The Stringifier can be set to allow for special output processing or tokenising.
 */
public interface PreProcessingStrategy {

	/**
	 * A construction method for a generic preprocessing strategy
	 * <br><br>
	 * Output is always normal formatted code
	 *
	 * @param name         the reference identifier to given to the strategy
	 * @param preProcessor IPreprocessor instance(s) to be executed
	 *
	 * @return Generic strategy for the passed parameters
	 */
	@SafeVarargs
	static PreProcessingStrategy of(String name, Class<? extends IGeneralPreProcessor>... preProcessor) {
		return new GenericGeneralPreProcessingStrategy(name, false, Arrays.asList(preProcessor));
	}

	/**
	 * A construction method for a generic preprocessing strategy
	 * <br><br>
	 * Output can be either formatted code or tokenised depending on the boolean flag value
	 *
	 * @param name         the reference identifier to given to the strategy
	 * @param tokenise     output will be tokenised
	 * @param preProcessor IPreprocessor instance(s) to be executed
	 *
	 * @return Generic strategy for the passed parameters
	 */
	@SafeVarargs
	static PreProcessingStrategy of(String name, boolean tokenise, Class<? extends IGeneralPreProcessor>... preProcessor) {
		return new GenericGeneralPreProcessingStrategy(name, tokenise, Arrays.asList(preProcessor));
	}

	/**
	 * A construction method for a generic preprocessing strategy
	 *
	 * @param name         the reference identifier to given to the strategy
	 * @param preProcessor IPreprocessor instance(s) to be executed
	 *
	 * @return Generic strategy for the passed parameters
	 */
	static PreProcessingStrategy of(String name, Class<? extends IAdvancedPreProcessorGroup> preProcessor) {
		return new GenericAdvancedPreProcessingStrategy(name, preProcessor);
	}

	/**
	 * Fetches the reference identifier for the strategy
	 *
	 * @return identifier string
	 */
	String getName();

	/**
	 * Fetches the ordered preprocessor classes in a list, to be run in order
	 * <br><br>
	 * If using an advanced preprocessor, this <b>must</b> return a singleton list (list with 1 element)
	 *
	 * @return ordered PreProcessor  class(es)
	 */
	List<Class<? extends IPreProcessor>> getPreProcessorClasses();

	/**
	 * Stringifier to use before passing the file to the detection algorithm, null for the default formatted code
	 *
	 * @return instance of the stringifier
	 */
	default ITokenStringifier getStringifier() {
		return null;
	}

	/**
	 * Is the strategy using an IAdvancedPreProcessor instance?
	 *
	 * @return boolean is advanced
	 */
	boolean isAdvanced();

	/**
	 * Generic implementation of the PreProcessingStrategy interface
	 */
	class GenericGeneralPreProcessingStrategy implements PreProcessingStrategy {

		private String name;
		private boolean tokenise;
		private List<Class<? extends IPreProcessor>> preProcessors;

		private GenericGeneralPreProcessingStrategy(String name, boolean tokenise, List<Class<? extends IGeneralPreProcessor>> preProcessors) {
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
		public boolean isAdvanced() {
			return false;
		}

		public boolean isResultTokenised() {
			return this.tokenise;
		}
	}

	/**
	 * Generic implementation of the PreProcessingStrategy interface for handling advanced preprocessors
	 */
	class GenericAdvancedPreProcessingStrategy implements PreProcessingStrategy {

		private String name;
		private Class<? extends IAdvancedPreProcessorGroup> preProcessor;

		private GenericAdvancedPreProcessingStrategy(String name, Class<? extends IAdvancedPreProcessorGroup> preProcessor) {
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
		public boolean isAdvanced() {
			return true;
		}
	}

}
