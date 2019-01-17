package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

public class DetectorNotFound extends Exception {
    public DetectorNotFound(String errorMessage) {
        super(errorMessage);
    }
}