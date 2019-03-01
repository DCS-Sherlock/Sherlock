package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown if the user attempts to load a page that isonly allowed
 * to be loaded through ajax/javascript requests
 */
public class NotAjaxRequest extends Exception {
    public NotAjaxRequest(String errorMessage) {
        super(errorMessage);
    }
}