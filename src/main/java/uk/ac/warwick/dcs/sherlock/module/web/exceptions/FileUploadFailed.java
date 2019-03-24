package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown if the file failed to upload when adding a submission
 */
public class FileUploadFailed extends Exception {
    public FileUploadFailed(String errorMessage) {
        super(errorMessage);
    }
}