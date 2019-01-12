package uk.ac.warwick.dcs.sherlock.api.annotation;

import java.lang.annotation.*;

/**
 * Annotation to define a parameter as adjustable by the UI. Currently must be a float or int.
 * <p><p>
 *  Can be used in classes which implement {@link uk.ac.warwick.dcs.sherlock.api.model.detection.IDetector} or {@link uk.ac.warwick.dcs.sherlock.api.model.postprocessing.IPostProcessor}
 * <p><p>
 * If another type is required please request it on https://github.com/DCS-Sherlock/Sherlock/issues
 * <p><p>
 * Set the parameter declaration to the desired default value
 * <p><p>
 * The engine will set this parameter to it's adjusted value when creating an instance of a supported object
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.FIELD)
public @interface AdjustableParameter {

	// float and int supported currently
	// To add more supported types look in the engine.core.registry

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
