package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

public class LoadingHelpFailed extends Exception {
    public LoadingHelpFailed(String errorMessage) {
        super(errorMessage);
    }
}