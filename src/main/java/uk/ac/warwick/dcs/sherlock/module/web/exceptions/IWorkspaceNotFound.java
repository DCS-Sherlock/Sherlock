package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

public class IWorkspaceNotFound extends Exception {
    public IWorkspaceNotFound(String errorMessage) {
        super(errorMessage);
    }
}