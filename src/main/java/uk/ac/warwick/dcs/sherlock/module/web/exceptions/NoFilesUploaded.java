package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

public class NoFilesUploaded extends Exception {
    public NoFilesUploaded(String errorMessage) {
        super(errorMessage);
    }
}