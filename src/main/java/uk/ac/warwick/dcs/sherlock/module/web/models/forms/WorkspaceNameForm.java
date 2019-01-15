package uk.ac.warwick.dcs.sherlock.module.web.models.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class WorkspaceNameForm {

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

    public WorkspaceNameForm() { }

    public WorkspaceNameForm(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
