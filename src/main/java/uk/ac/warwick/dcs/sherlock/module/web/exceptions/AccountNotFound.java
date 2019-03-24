package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown when the account is not found on the admin settings page
 */
public class AccountNotFound extends Exception {
    public AccountNotFound(String errorMessage) {
        super(errorMessage);
    }
}