package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown if the source file is not found in the current submission
 */
public class SourceFileNotFound extends Exception {
    public SourceFileNotFound(String errorMessage) {
        super(errorMessage);
    }
}