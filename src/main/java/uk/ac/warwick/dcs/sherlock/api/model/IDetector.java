package uk.ac.warwick.dcs.sherlock.api.model;

import uk.ac.warwick.dcs.sherlock.api.model.data.IModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.data.IModelResultItem;

import java.lang.annotation.*;
import java.util.*;

/**
 * Interface for implementing a detection algorithm
 */
public interface IDetector {

	/**
	 * Builds a set of workers on a passed dataset, these workers are executed in parallel to produce the algorithm result
	 *
	 * @param data            preprocessed dataset
	 * @param resultItemClass class the worker should use to return its results
	 *
	 * @return list of configured workers ready to be executed
	 */
	List<IDetectorWorker> buildWorkers(List<IModelDataItem> data, Class<? extends IModelResultItem> resultItemClass);

	/**
	 * @return display name of the algorithm
	 */
	String getDisplayName();

	/**
	 * Returns the appropriate lexer for this strategy and the language of the source files	 *
	 *
	 * @param lang the language of the source files
	 *
	 * @return the lexer class to use
	 */
	Class<? extends org.antlr.v4.runtime.Lexer> getLexer(Language lang);

	/**
	 * Allows implementation to override the default post processor
	 *
	 * @return the post processor to use, default null
	 */
	default Class<? extends IPostProcessor> getPostProcessor() {
		return null;
	}

	/**
	 * Allows implementation to specify the preprocessors required. The string name of each of the strategies returned is used as the key reference in the preprocessed dataset given to the {@link
	 * IDetector#buildWorkers(List, Class)} method
	 *
	 * @return a list of the required preprocessing strategies
	 */
	List<IPreProcessingStrategy> getPreProcessors();

	/**
	 * @return Array of languages supported by the algorithm
	 */
	Language[] getSupportedLanguages();

	/**
	 * Top level interface workers are required to implement
	 */
	interface IDetectorWorker {

		/**
		 * Do work and create the results
		 */
		void execute();

		/**
		 * Gets the results of the worker execution, only minimal processing should be performed in this method
		 *
		 * @return worker results
		 */
		IModelResultItem getResult();

	}

	/**
	 * Annotation to define a parameter as adjustable by the UI. Currently must be a float, or an int.
	 * If another type is required please request it on {@link https://github.com/DCS-Sherlock/Sherlock/issues}
	 */
	@Documented
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.FIELD)
	@interface TuneableParameter {

		/**
		 * The default value to load into the field
		 */
		float defaultValue();

		/**
		 * The maximum bound for the field
		 */
		float maxumumBound();

		/**
		 * Minimum bound for field
		 */
		float minimumBound();

	}

}
