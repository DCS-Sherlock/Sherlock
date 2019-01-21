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

	String name() default "";

	Side side() default Side.UNKNOWN;

	String version() default "";

	@Documented
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.FIELD)
	@interface Instance {

	}
}
