package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

public class ResultsNotFound extends Exception {
    public ResultsNotFound(String errorMessage) {
        super(errorMessage);
    }
}