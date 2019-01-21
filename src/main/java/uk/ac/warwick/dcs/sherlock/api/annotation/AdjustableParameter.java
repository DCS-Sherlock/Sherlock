package uk.ac.warwick.dcs.sherlock.api.annotation;

import java.lang.annotation.*;

/**
 * Annotation to define a parameter as adjustable by the UI. Currently must be a float or int.
 * <br><br>
 * Can be used in classes which implement {@link uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector} or {@link uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor}
 * <br><br>
 * If another type is required please request it on https://github.com/DCS-Sherlock/Sherlock/issues
 * <br><br>
 * Set the parameter declaration to the desired default value
 * <br><br>
 * The engine will set this parameter to it's adjusted value when creating an instance of a supported object
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.FIELD)
public @interface AdjustableParameter {

	// float and int supported currently
	// To add more supported types look in the engine.core.registry

	/**
	 * default value the parameter takes
	 *
	 * @return the default value
	 */
	float defaultValue();

	/**
	 * Optional, detailed description of what the parameter does
	 *
	 * @return the description string
	 */
	String description() default "";

	/**
	 * The maximum bound for the field
	 *
	 * @return the max bound
	 */
	float maxumumBound();

	/**
	 * Minimum bound for field
	 *
	 * @return the min bound
	 */
	float minimumBound();

	/**
	 * Name for the parameter to be displayed in the UI
	 *
	 * @return the parameter name
	 */
	String name();

	/**
	 * The step to increment or decrement the parameter by in the UI
	 *
	 * @return the parameter step
	 */
	float step();
}
