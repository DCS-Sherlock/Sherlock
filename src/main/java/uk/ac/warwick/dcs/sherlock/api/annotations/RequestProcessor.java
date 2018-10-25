package uk.ac.warwick.dcs.sherlock.api.annotations;

import uk.ac.warwick.dcs.sherlock.api.request.RequestDatabase;

import java.lang.annotation.*;

@Documented
@Retention (RetentionPolicy.RUNTIME)
@Target (ElementType.TYPE)
public @interface RequestProcessor {

	String apiFieldName() default "";

	Class<?> databaseClass() default RequestDatabase.class;

	@Documented
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.FIELD)
	@interface Instance {

	}

	@Documented
	@Retention (RetentionPolicy.RUNTIME)
	@Target (ElementType.METHOD)
	@interface PostHandler {

	}
}
