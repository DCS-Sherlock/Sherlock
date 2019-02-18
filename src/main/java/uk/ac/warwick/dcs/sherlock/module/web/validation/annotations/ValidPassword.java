package uk.ac.warwick.dcs.sherlock.module.web.validation.annotations;

import uk.ac.warwick.dcs.sherlock.module.web.validation.validators.ValidPasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidPasswordValidator.class)
public @interface ValidPassword {
    String message() default "{error.current_password.invalid}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}