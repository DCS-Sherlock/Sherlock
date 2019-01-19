package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

public class NotAjaxRequest extends Exception {
    public NotAjaxRequest(String errorMessage) {
        super(errorMessage);
    }
}