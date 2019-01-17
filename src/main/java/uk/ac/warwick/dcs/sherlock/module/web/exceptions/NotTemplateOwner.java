package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

public class NotTemplateOwner extends Exception {
    public NotTemplateOwner(String errorMessage) {
        super(errorMessage);
    }
}