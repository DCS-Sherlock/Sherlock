package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

public class TemplateContainsNoDetectors extends Exception {
    public TemplateContainsNoDetectors(String errorMessage) {
        super(errorMessage);
    }
}