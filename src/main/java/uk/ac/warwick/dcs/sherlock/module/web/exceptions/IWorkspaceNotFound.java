package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown if the workspace is not found in the engine
 */
public class IWorkspaceNotFound extends Exception {
    public IWorkspaceNotFound(String errorMessage) {
        super(errorMessage);
    }
}