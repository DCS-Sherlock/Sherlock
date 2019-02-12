package uk.ac.warwick.dcs.sherlock.module.web.validation.validators;

import uk.ac.warwick.dcs.sherlock.module.web.models.forms.AccountPasswordForm;
import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.PasswordsMatch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, AccountPasswordForm> {
    public PasswordsMatchValidator() { }

    public void initialize(AccountPasswordForm constraint) { }

    public boolean isValid(AccountPasswordForm passwordForm, ConstraintValidatorContext context) {
        if (passwordForm.getNewPassword() == null || passwordForm.getConfirmPassword() == null) {
            return false;
        }
        return passwordForm.getNewPassword().equals(passwordForm.getConfirmPassword());
    }
}
