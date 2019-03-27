package uk.ac.warwick.dcs.sherlock.module.web.data.models.forms;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * The form to upload submission(s)
 */
public class SubmissionsForm {
    @NotEmpty(message = "{error.file.empty}")
    public MultipartFile[] files;

    @NotNull(message = "{error.single.empty}")
    public boolean single;

    public SubmissionsForm() { }

    public SubmissionsForm(MultipartFile[] files, boolean single) {
        this.files = files;
        this.single = single;
    }

    public MultipartFile[] getFiles() {
        return this.files;
    }

    public void setFiles(MultipartFile[] files) {
        this.files = files;
    }

    public boolean isSingle() {
        return single;
    }

    public boolean getSingle() {
        return single;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }
}
