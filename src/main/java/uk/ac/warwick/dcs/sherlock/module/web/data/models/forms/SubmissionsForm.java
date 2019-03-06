package uk.ac.warwick.dcs.sherlock.module.web.data.models.forms;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

public class SubmissionsForm {

    @NotEmpty(message = "{error.file.empty}")
    public MultipartFile[] files;

    public SubmissionsForm() { }

    public SubmissionsForm(MultipartFile[] files) {
        this.files = files;
    }

    public MultipartFile[] getFiles() {
        return this.files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }
}
