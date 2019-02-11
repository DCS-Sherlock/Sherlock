package uk.ac.warwick.dcs.sherlock.module.web.models.forms;

import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.PasswordsMatch;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@PasswordsMatch
public class PasswordForm {
    @NotNull(message = "{error_old_password_invalid}")
    public String oldPassword;

    @NotNull(message = "{error_password_empty}")
    @Size.List({
            @Size(
                    min = 8,
                    message = "{error_password_empty}"),
            @Size(
                    max = 64,
                    message = "{error_password_length_max}")
    })
    public String newPassword;

    public String confirmPassword;

    public PasswordForm() { }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
