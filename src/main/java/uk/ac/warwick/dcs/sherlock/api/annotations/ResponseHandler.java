package uk.ac.warwick.dcs.sherlock.api.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.METHOD)
public @interface ResponseHandler {

}
