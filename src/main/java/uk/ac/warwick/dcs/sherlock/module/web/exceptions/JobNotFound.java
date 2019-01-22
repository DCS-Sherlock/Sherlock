package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

public class JobNotFound extends Exception {
    public JobNotFound(String errorMessage) {
        super(errorMessage);
    }
}