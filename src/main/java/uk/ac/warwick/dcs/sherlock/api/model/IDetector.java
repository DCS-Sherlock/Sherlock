package uk.ac.warwick.dcs.sherlock.api.model;

import uk.ac.warwick.dcs.sherlock.api.model.data.IModelRawResult;
import uk.ac.warwick.dcs.sherlock.api.model.data.ModelDataItem;

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
	 *
	 * @return list of configured workers ready to be executed
	 */
	List<IDetectorWorker> buildWorkers(List<ModelDataItem> data);

	/**
	 * @return display name of the algorithm
	 */
	String getDisplayName();

	/**
	 * @return the rank of the detector, either
	 */
	Rank getRank();

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
	 * @return the post processor to use
	 */
	//Class<? extends AbstractPostProcessor> getPostProcessor();

	/**
	 * Specify the preprocessors required for this detector.
	 * <p><p>
	 * The individual strategies in the list can be produced using the generic methods {@link IPreProcessingStrategy#of(String, Class... )} or {@link IPreProcessingStrategy#of(String, boolean, Class...)}
	 * in the interface, or using a fully custom {@link IPreProcessingStrategy} class.
	 * <p><p>
	 * The string name of each of the strategies is used as the key reference in the preprocessed dataset given to the {@link IDetector#buildWorkers(List)} method
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
		IModelRawResult getRawResult();

	}

	enum Rank {
		PRIMARY, BACKUP
	}

	/**
	 * Annotation to define a parameter as adjustable by the UI. Currently must be a float or int.
	 * <p><p>
	 * If another type is required please request it on https://github.com/DCS-Sherlock/Sherlock/issues
	 * <p><p>
	 * Set the parameter declaration to the desired default value
	 * <p><p>
	 * The engine will set this parameter to it's adjusted value when creating an instance of an IDetector implementatiomn
	 */
	@Documented
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.FIELD)
	@interface DetectorParameter {

		/**
		 * Name for the parameter to be displayed in the UI
		 */
		String name();

		/**
		 * Default value the parameter takes
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

		/**
		 * The step to increment or decrement the parameter by in the UI
		 */
		float step();

		/**
		 * Optional, detailed description of what the parameter does
		 */
		String description() default "";
	}

}
