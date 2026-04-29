package com.bank.account.domain.exception;

/**
 * Exception thrown when an invalid account type is provided.
 * Account type must be "Ahorro" (Savings) or "Corriente" (Checking).
 */
public class InvalidAccountTypeException extends RuntimeException {

    public InvalidAccountTypeException(String message) {
        super(message);
    }

    public InvalidAccountTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
