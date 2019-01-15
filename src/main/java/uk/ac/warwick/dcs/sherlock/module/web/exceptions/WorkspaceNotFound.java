package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

public class WorkspaceNotFound extends Exception {
    public WorkspaceNotFound(String errorMessage) {
        super(errorMessage);
    }
}