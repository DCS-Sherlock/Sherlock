package uk.ac.warwick.dcs.sherlock.api.annotation;

import uk.ac.warwick.dcs.sherlock.api.util.Side;

import java.lang.annotation.*;

/**
 * Marks a method as an event handler. If the containing class object is registered on the event bus, the method will receive events of the type of its required single parameter.
 * <br><br>
 * Set the side parameter to only receive events when sherlock is running as a server or a client
 */
@Documented
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.METHOD)
public @interface EventHandler {

	Side side() default Side.UNKNOWN;

}
