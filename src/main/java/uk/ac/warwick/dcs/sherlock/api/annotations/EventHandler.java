package uk.ac.warwick.dcs.sherlock.api.annotations;

import uk.ac.warwick.dcs.sherlock.api.util.Side;

import java.lang.annotation.*;

@Documented
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.METHOD)
public @interface EventHandler {

	Side side() default Side.UNKNOWN;

}
