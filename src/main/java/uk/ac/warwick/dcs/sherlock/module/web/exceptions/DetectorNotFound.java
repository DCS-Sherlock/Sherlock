package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown when the detector was not found
 */
public class DetectorNotFound extends Exception {
    public DetectorNotFound(String errorMessage) {
        super(errorMessage);
    }
}