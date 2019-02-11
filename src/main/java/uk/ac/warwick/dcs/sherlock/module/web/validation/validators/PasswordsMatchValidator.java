package uk.ac.warwick.dcs.sherlock.module.web.validation.validators;

import uk.ac.warwick.dcs.sherlock.module.web.models.forms.PasswordForm;
import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.PasswordsMatch;
import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.ValidLanguage;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordsMatchValidator implements ConstraintValidator<PasswordsMatch, PasswordForm> {
    public PasswordsMatchValidator() { }

    public void initialize(PasswordForm constraint) { }

    public boolean isValid(PasswordForm passwordForm, ConstraintValidatorContext context) {
        if (passwordForm.getNewPassword() == null || passwordForm.getConfirmPassword() == null) {
            return false;
        }
        return passwordForm.getNewPassword().equals(passwordForm.getConfirmPassword());
    }
}
