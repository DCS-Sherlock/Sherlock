package uk.ac.warwick.dcs.sherlock.module.web.models.forms;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;

public class SubmissionsForm {

    @NotEmpty(message = "{error_file_empty}")
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
