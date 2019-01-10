package uk.ac.warwick.dcs.sherlock.api.model.postprocessing;

import java.lang.annotation.*;
import java.util.*;

public interface IPostProcessor {

	void loadRawResults(List<AbstractModelTaskRawResult> results);

	/**
	 * Run the post processing and return a data item with the final results in the correct format
	 *
	 * TODO: look to make it possible to support multiple scorers in a single PostProcessor depending on the detector used
	 */
	ModelTaskProcessedResults processResults();

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
	@interface PostProcessParameter {

		/**
		 * Default value the parameter takes
		 */
		float defaultValue();

		/**
		 * Optional, detailed description of what the parameter does
		 */
		String description() default "";

		/**
		 * The maximum bound for the field
		 */
		float maxumumBound();

		/**
		 * Minimum bound for field
		 */
		float minimumBound();

		/**
		 * Name for the parameter to be displayed in the UI
		 */
		String name();

		/**
		 * The step to increment or decrement the parameter by in the UI
		 */
		float step();
	}
}
