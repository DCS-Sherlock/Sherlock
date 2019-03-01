package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown if the parameter is not found for the current user
 */
public class ParameterNotFound extends Exception {
    public ParameterNotFound(String errorMessage) {
        super(errorMessage);
    }
}