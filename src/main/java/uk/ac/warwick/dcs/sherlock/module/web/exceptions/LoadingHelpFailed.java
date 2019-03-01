package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown if the help.properties file was not loaded successfully
 */
public class LoadingHelpFailed extends Exception {
    public LoadingHelpFailed(String errorMessage) {
        super(errorMessage);
    }
}