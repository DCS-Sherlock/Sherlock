package uk.ac.warwick.dcs.sherlock.api.annotations;

import uk.ac.warwick.dcs.sherlock.api.request.RequestDatabase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.TYPE)
public @interface RequestProcessor {

	String apiFieldName() default "";

	Class<?> databaseClass() default RequestDatabase.class;

	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.FIELD)
	@interface Instance {

	}

	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.METHOD)
	@interface PostHandler {

	}
}
