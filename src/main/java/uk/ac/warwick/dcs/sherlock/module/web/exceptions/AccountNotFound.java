package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

public class AccountNotFound extends Exception {
    public AccountNotFound(String errorMessage) {
        super(errorMessage);
    }
}