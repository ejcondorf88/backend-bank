package com.bank.account.domain.exception;

/**
 * Exception thrown when a requested movement is not found.
 * Part of the domain layer for F2/F4 requirements.
 */
public class MovementNotFoundException extends RuntimeException {

    public MovementNotFoundException(Long id) {
        super("Movement not found with id: " + id);
    }

    public MovementNotFoundException(String accountNumber) {
        super("Movement not found for account: " + accountNumber);
    }

    public MovementNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
