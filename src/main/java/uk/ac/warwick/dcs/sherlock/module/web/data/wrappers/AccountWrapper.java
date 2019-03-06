package uk.ac.warwick.dcs.sherlock.module.web.data.wrappers;

import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Account;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Role;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Template;
import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Workspace;

import java.util.Set;

/*
    Somehow if you use form binding to set a variable with the same name as a variable in the
    Account object, the variable in the @ModelAttribute("account") instance of the Account
    object is overwritten.

    e.g. if you have a form with a variable called "email", the email variable in
    @ModelAttribute("account") is being overwritten with the result of the form binding.

    Therefore, @ModelAttribute("account") now returns AccountWrapper instead of Account to
    prevent variables being overwritten. All get functions in this wrapper must match
    those in the Account object.
 */
public class AccountWrapper {
    private Account wrapperAccount;

    public AccountWrapper() {
        this.wrapperAccount = new Account();
    }

    public AccountWrapper(Account account) {
        this.wrapperAccount = account;
    }

    public long getId() {
        return this.wrapperAccount.getId();
    }

    public String getEmail() {
        return this.wrapperAccount.getEmail();
    }

    public String getPassword() {
        return this.wrapperAccount.getPassword();
    }

    public Set<Role> getRoles() {
        return this.wrapperAccount.getRoles();
    }

    public String getUsername() {
        return this.wrapperAccount.getUsername();
    }

    public Set<Workspace> getWorkspaces() {
        return this.wrapperAccount.getWorkspaces();
    }

    public Set<Template> getTemplates() {
        return this.wrapperAccount.getTemplates();
    }

    public Account getAccount() {
        return this.wrapperAccount;
    }
}