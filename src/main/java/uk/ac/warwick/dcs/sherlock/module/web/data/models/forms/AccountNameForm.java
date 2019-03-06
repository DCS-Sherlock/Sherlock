package uk.ac.warwick.dcs.sherlock.module.web.data.models.forms;

import uk.ac.warwick.dcs.sherlock.module.web.data.models.db.Account;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class AccountNameForm {

    @NotNull(message = "{error.name.empty}")
    @Size.List({
            @Size(
                    min = 1,
                    message = "{error.name.empty}"),
            @Size(
                    max = 64,
                    message = "{error.name.max_length}")
    })
    public String username;

    public AccountNameForm() { }

    public AccountNameForm(Account account){
        this.username = account.getUsername();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
