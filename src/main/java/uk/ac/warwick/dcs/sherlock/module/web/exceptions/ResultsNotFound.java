package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown if the job id is not found for the current workspace
 */
public class ResultsNotFound extends Exception {
    public ResultsNotFound(String errorMessage) {
        super(errorMessage);
    }
}