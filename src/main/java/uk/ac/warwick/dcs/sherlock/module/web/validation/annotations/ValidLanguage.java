package uk.ac.warwick.dcs.sherlock.module.web.validation.annotations;

import uk.ac.warwick.dcs.sherlock.module.web.validation.validators.ValidLanguageValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines the annotation of the validator that checks if the language
 * supplied exists in the sherlock engine
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidLanguageValidator.class)
public @interface ValidLanguage {
    /**
     * Defines the key of the error message in the localisation file
     * if this validation step fails
     *
     * @return the message key
     */
    String message() default "{error.language.not_found}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}