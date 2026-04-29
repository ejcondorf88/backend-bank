package com.bank.account.domain.exception;

/**
 * Exception thrown when an invalid movement type is provided.
 * Valid types are: "Deposito" and "Retiro".
 * Part of the domain layer for F2 requirements.
 */
public class InvalidMovementTypeException extends RuntimeException {

    public InvalidMovementTypeException(String type) {
        super("Invalid movement type: " + type + ". Allowed types: Deposito, Retiro");
    }

    public InvalidMovementTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
