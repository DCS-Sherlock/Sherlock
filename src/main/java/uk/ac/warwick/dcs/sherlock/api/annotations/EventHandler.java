package uk.ac.warwick.dcs.sherlock.api.annotations;

import uk.ac.warwick.dcs.sherlock.api.util.Side;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.METHOD)
public @interface EventHandler {

	Side side() default Side.UNKNOWN;

}
