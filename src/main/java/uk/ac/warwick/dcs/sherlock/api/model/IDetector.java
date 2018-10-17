package uk.ac.warwick.dcs.sherlock.api.model;

import uk.ac.warwick.dcs.sherlock.api.model.data.IModelDataItem;
import uk.ac.warwick.dcs.sherlock.api.model.data.IModelResultItem;

import java.util.List;

/**
 * Interface for implementing a detection algorithm
 */
public interface IDetector {

	/**
	 * Builds a set of workers on a passed dataset, these workers are executed in parallel to produce the algorithm result
	 * @param data preprocessed dataset
	 * @param resultItemClass class the worker should use to return its results
	 * @return list of configured workers ready to be executed
	 */
	List<IDetectorWorker> buildWorkers(List<IModelDataItem> data, Class<? extends IModelResultItem> resultItemClass);

	/**
	 * @return display name of the algorithm
	 */
	String getDisplayName();

	/**
	 * Returns the appropriate lexer for this strategy and the language of the source files	 *
	 * @param lang the language of the source files
	 * @return the lexer class to use
	 */
	Class<? extends org.antlr.v4.runtime.Lexer> getLexer(Language lang);

	/**
	 * Allows implementation to override the default post processor
	 * @return the post processor to use, default null
	 */
	default Class<? extends IPostProcessor> getPostProcessor() {
		return null;
	}

	/**
	 * Allows implementation to specify the preprocessors required. The string name of each of the strategies returned is used as the key reference in the preprocessed dataset given to the {@link IDetector#buildWorkers(List, Class)} method
	 * @return a list of the required preprocessing strategies
	 */
	List<IPreProcessingStrategy> getPreProcessors();

	/**
	 * @return Stream of the languages supported by the algorithm
	 */
	List<Language> getSupportedLanguages();

	/**
	 * Top level interface workers are required to implement
	 */
	interface IDetectorWorker {

		/**
		 * Gets the results of the worker execution, only minimal processing should be performed in this method
		 * @return worker results
		 */
		IModelResultItem getResult();

		/**
		 * Do work and create the results
		 */
		void execute();

	}

}
