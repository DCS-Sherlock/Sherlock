package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown if the template is not found for the current user
 */
public class TemplateNotFound extends Exception {
    public TemplateNotFound(String errorMessage) {
        super(errorMessage);
    }
}