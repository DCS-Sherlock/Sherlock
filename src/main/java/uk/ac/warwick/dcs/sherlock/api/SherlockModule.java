package uk.ac.warwick.dcs.sherlock.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.TYPE)
public @interface SherlockModule {

	String name() default "";

	String version() default "";

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@interface EventHandler {}
}
