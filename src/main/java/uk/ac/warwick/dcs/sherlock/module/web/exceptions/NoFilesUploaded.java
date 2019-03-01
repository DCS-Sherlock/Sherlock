package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown if the user clicks submit on the "upload submissions"
 * page when they selected no files
 */
public class NoFilesUploaded extends Exception {
    public NoFilesUploaded(String errorMessage) {
        super(errorMessage);
    }
}