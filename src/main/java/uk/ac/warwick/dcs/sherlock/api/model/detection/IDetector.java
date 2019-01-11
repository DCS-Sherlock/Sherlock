package uk.ac.warwick.dcs.sherlock.api.model.detection;

import uk.ac.warwick.dcs.sherlock.api.model.postprocessing.AbstractModelTaskRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IPreProcessingStrategy;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.Language;

import java.util.*;

/**
 * Interface for implementing a detection algorithm
 *
 * Supports adjustable parameters see {@link uk.ac.warwick.dcs.sherlock.api.annotation.AdjustableParameter}
 */
public interface IDetector {

	/**
	 * Builds a set of workers on a passed dataset, these workers are executed in parallel to produce the algorithm result
	 *
	 * @param data preprocessed dataset
	 *
	 * @return list of configured workers ready to be executed
	 */
	List<IDetectorWorker> buildWorkers(List<ModelDataItem> data);

	/**
	 * @return display name of the algorithm
	 */
	String getDisplayName();

	/**
	 * Returns the appropriate lexer for this strategy and the language of the source files
	 *
	 * @param lang the language of the source files
	 *
	 * @return the lexer class to use
	 */
	Class<? extends org.antlr.v4.runtime.Lexer> getLexer(Language lang);

	/**
	 * Returns the appropriate parser for this strategy and the language of the source files
	 *
	 * @param lang the language of the source files
	 *
	 * @return the parser class to use
	 */
	Class<? extends org.antlr.v4.runtime.Parser> getParser(Language lang);

	/**
	 * Specify the preprocessors required for this detector.
	 * <p><p>
	 * The individual strategies in the list can be produced using the generic methods {@link IPreProcessingStrategy#of(String, Class...)} or {@link IPreProcessingStrategy#of(String, boolean,
	 * Class...)} in the interface, or using a fully custom {@link IPreProcessingStrategy} class.
	 * <p><p>
	 * The string name of each of the strategies is used as the key reference in the preprocessed dataset given to the {@link IDetector#buildWorkers(List)} method
	 *
	 * @return a list of the required preprocessing strategies
	 */
	List<IPreProcessingStrategy> getPreProcessors();

	/**
	 * @return the post processor to use
	 */
	//Class<? extends AbstractPostProcessor> getPostProcessor();

	/**
	 * @return the rank of the detector, either
	 */
	Rank getRank();

	/**
	 * @return Array of languages supported by the algorithm
	 */
	Language[] getSupportedLanguages();

	enum Rank {
		PRIMARY, BACKUP
	}

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
		AbstractModelTaskRawResult getRawResult();

	}

}
