package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

public class FileUploadFailed extends Exception {
    public FileUploadFailed(String errorMessage) {
        super(errorMessage);
    }
}