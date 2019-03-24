package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown if the user tries to compare a submission against itself
 */
public class CompareSameSubmission extends Exception {
    public CompareSameSubmission(String errorMessage) {
        super(errorMessage);
    }
}