package uk.ac.warwick.dcs.sherlock.module.web.models.forms;

import uk.ac.warwick.dcs.sherlock.module.web.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.models.db.Role;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

public class AccountForm {
    @NotNull(message = "{error_name_empty}")
    @Size.List({
            @Size(
                    min = 1,
                    message = "{error_name_empty}"),
            @Size(
                    max = 64,
                    message = "{error_name_length_max}")
    })
    public String name;

    @Size(min = 1, message = "{error_email_empty}")
    @Email(message = "{error_email_invalid}")
    public String email;

    @NotNull(message = "{error_admin_empty}")
    public boolean isAdmin;

    @NotNull(message = "{error_old_password_invalid}")
    public String oldPassword;

    public AccountForm() { }

    public AccountForm(Account account) {
        this.name = account.getUsername();
        this.email = account.getEmail();

        this.isAdmin = false;
        Set<Role> roles = account.getRoles();
        for (Role role : roles) {
            if (role.getName().equals("ADMIN")) {
                this.isAdmin = true;
            }
        }
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean admin) {
        isAdmin = admin;
    }
}
