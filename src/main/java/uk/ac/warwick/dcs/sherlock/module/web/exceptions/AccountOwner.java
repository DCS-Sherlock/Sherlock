package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

/**
 * Thrown when a user attempts to modify their own account
 * through the admin account settings page
 */
public class AccountOwner extends Exception {
    public AccountOwner(String errorMessage) {
        super(errorMessage);
    }
}