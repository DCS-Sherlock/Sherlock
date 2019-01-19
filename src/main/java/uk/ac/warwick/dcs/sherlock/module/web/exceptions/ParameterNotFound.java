package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

public class ParameterNotFound extends Exception {
    public ParameterNotFound(String errorMessage) {
        super(errorMessage);
    }
}