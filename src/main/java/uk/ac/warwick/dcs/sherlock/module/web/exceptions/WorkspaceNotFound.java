package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown if the workspace is not found for the current user
 */
public class WorkspaceNotFound extends Exception {
    public WorkspaceNotFound(String errorMessage) {
        super(errorMessage);
    }
}