package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown if the user attempts to create a submission with a name that already exists in the workspace
 */
public class DuplicateSubmissionNames extends Exception {
    public DuplicateSubmissionNames(String errorMessage) {
        super(errorMessage);
    }
}