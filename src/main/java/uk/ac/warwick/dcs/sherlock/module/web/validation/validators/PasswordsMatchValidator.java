package uk.ac.warwick.dcs.sherlock.module.web.validation.validators;

import uk.ac.warwick.dcs.sherlock.module.web.models.forms.AccountPasswordForm;
import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.PasswordsMatch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Form validator that checks if the new password and confirm password
 * fields match
 */
public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, AccountPasswordForm> {
    public PasswordsMatchValidator() { }

    public void initialize(AccountPasswordForm constraint) { }

    /**
     * Performs the validation step to check if the two inputs are set
     * and then that they equal
     *
     * @param passwordForm the password form
     * @param context (not used here)
     *
     * @return whether or not the validation passed
     */
    public boolean isValid(AccountPasswordForm passwordForm, ConstraintValidatorContext context) {
        if (passwordForm.getNewPassword() == null || passwordForm.getConfirmPassword() == null) {
            return false;
        }
        return passwordForm.getNewPassword().equals(passwordForm.getConfirmPassword());
    }
}
