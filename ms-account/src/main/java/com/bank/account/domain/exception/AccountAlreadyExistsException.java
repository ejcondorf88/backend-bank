package com.bank.account.domain.exception;

/**
 * Exception thrown when attempting to create an account with a number that already exists.
 * Account numbers must be unique across the system.
 */
public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException(String accountNumber) {
        super("Account already exists with number: " + accountNumber);
    }
}
