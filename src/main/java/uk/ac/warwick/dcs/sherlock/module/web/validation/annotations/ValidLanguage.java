package uk.ac.warwick.dcs.sherlock.module.web.validation.annotations;

import uk.ac.warwick.dcs.sherlock.module.web.validation.validators.ValidLanguageValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidLanguageValidator.class)
public @interface ValidLanguage {
    String message() default "{error_language_not_found}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}