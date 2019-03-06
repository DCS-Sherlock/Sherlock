package uk.ac.warwick.dcs.sherlock.module.web.data.models.forms;

import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Role;
import uk.ac.warwick.dcs.sherlock.module.web.validation.annotations.ValidPassword;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

public class AccountForm {
    @NotNull(message = "{error.name.empty}")
    @Size.List({
            @Size(
                    min = 1,
                    message = "{error.name.empty}"),
            @Size(
                    max = 64,
                    message = "{error.name.max_length}")
    })
    public String name;

    @Size(min = 1, message = "{error.email.empty}")
    @Email(message = "{error.email.invalid}")
    public String email;

    @NotNull(message = "{error.admin.empty}")
    public boolean isAdmin;

    @NotNull(message = "{error.current_password.invalid}")
    @ValidPassword
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
