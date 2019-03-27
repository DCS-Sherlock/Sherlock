package uk.ac.warwick.dcs.sherlock.module.web.data.models.forms;

import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.ValidPassword;

/**
 * The generic confirm password form
 */
public class PasswordForm {

    @ValidPassword
    public String confirmPassword;

    public PasswordForm() { }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
