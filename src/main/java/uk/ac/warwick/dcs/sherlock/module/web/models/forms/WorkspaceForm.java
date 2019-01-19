package uk.ac.warwick.dcs.sherlock.module.web.models.forms;

import uk.ac.warwick.dcs.sherlock.module.web.models.wrapper.WorkspaceWrapper;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class WorkspaceForm {

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

    @NotNull(message = "{error_language_empty}")
    public String language; //TODO: perform validation to check if input is a valid language

    public WorkspaceForm() { }

    public WorkspaceForm(WorkspaceWrapper workspaceWrapper) {
        this.name = workspaceWrapper.getName();
        this.language = workspaceWrapper.getLanguage();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
