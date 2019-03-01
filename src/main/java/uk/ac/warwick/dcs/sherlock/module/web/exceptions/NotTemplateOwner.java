package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown if a user attempts to modify a template that they are not
 * the owner of
 */
public class NotTemplateOwner extends Exception {
    public NotTemplateOwner(String errorMessage) {
        super(errorMessage);
    }
}