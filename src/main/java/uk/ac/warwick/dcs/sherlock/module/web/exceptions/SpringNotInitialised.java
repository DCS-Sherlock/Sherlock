package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown when Spring hasn't finished loading but a user attempts to load a page
 */
public class SpringNotInitialised extends Exception {
    public SpringNotInitialised(String errorMessage) {
        super(errorMessage);
    }
}