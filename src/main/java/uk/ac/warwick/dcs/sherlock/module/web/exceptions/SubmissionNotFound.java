package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

public class SubmissionNotFound extends Exception {
    public SubmissionNotFound(String errorMessage) {
        super(errorMessage);
    }
}