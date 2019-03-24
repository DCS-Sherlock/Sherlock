package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown when the user tries to run a function that has not been finished
 */
public class NotImplementedException extends Exception {
    public NotImplementedException(String errorMessage) {
        super(errorMessage);
    }
}