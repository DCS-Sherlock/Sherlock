package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

public class TemplateNotFound extends Exception {
    public TemplateNotFound(String errorMessage) {
        super(errorMessage);
    }
}