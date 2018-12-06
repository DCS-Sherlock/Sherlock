package uk.ac.warwick.dcs.sherlock.api.annotation;

import java.lang.annotation.*;

@Documented
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.METHOD)
public @interface ResponseHandler {

}
