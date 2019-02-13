package uk.ac.warwick.dcs.sherlock.module.web.exceptions;

public class AccountOwner extends Exception {
    public AccountOwner(String errorMessage) {
        super(errorMessage);
    }
}