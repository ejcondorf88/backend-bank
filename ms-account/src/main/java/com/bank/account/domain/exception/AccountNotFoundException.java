package com.bank.account.domain.exception;

/**
 * Exception thrown when an account is not found in the system.
 */
public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String accountNumber) {
        super("Account not found with number: " + accountNumber);
    }

    public AccountNotFoundException(Long id) {
        super("Account not found with id: " + id);
    }
}
