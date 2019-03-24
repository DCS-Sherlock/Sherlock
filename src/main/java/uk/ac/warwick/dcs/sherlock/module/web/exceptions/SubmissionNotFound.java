package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown if the submission is not found in the current workspace
 */
public class SubmissionNotFound extends Exception {
    public SubmissionNotFound(String errorMessage) {
        super(errorMessage);
    }
}