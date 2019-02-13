package uk.ac.warwick.dcs.sherlock.module.web.models.forms;

public class PasswordForm {

    public String confirmPassword;

    public PasswordForm() { }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
