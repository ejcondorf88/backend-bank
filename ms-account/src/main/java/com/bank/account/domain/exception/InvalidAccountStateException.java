package com.bank.account.domain.exception;

/**
 * Exception thrown when an invalid state transition is attempted on an account.
 * Examples:
 * - Trying to activate an already active account
 * - Trying to deactivate an already inactive account
 * - Performing transactions on inactive accounts
 */
public class InvalidAccountStateException extends RuntimeException {

    public InvalidAccountStateException(String message) {
        super(message);
    }

    public InvalidAccountStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
