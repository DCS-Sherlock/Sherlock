package uk.ac.warwick.dcs.sherlock.module.web.validation.annotations;

import uk.ac.warwick.dcs.sherlock.module.web.validation.validators.PasswordsMatchValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordsMatchValidator.class)
public @interface PasswordsMatch {
    String message() default "{error.confirm_password.not_matching}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}