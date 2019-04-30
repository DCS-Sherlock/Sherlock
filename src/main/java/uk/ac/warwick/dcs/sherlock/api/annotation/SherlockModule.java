package uk.ac.warwick.dcs.sherlock.api.annotation;

import uk.ac.warwick.dcs.sherlock.api.util.Side;

import java.lang.annotation.*;

/**
 * Annotation to mark a module which should be register to the Sherlock engine on startup
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.TYPE)
public @interface SherlockModule {

	/**
	 * The name of the module, for easy identification
	 * @return name
	 */
	String name() default "";

	/**
	 * Is SherlockEngine running locally (Side.CLIENT) or on a server (Side.SERVER)
	 * @return side
	 */
	Side side() default Side.UNKNOWN;

	/**
	 * Version string for the module, help users track whether they are up to date
	 * @return version
	 */
	String version() default "";

	/**
	 * Annotation for an instance variable if required by the module code
	 * <br><br>
	 * Create a variable with the type of the module class, and use this annotation, it will be populated with created instance of the module.
	 */
	@Documented
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.FIELD)
	@interface Instance {

	}
}
