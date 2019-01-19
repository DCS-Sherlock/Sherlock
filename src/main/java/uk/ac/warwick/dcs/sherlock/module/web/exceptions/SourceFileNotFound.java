package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

public class SourceFileNotFound extends Exception {
    public SourceFileNotFound(String errorMessage) {
        super(errorMessage);
    }
}