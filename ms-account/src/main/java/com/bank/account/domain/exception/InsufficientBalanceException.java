package com.bank.account.domain.exception;

/**
 * Exception thrown when attempting a withdrawal that exceeds the available balance.
 * This implements the F3 requirement: "Saldo no disponible" validation.
 */
public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(String message) {
        super(message);
    }

    public InsufficientBalanceException(String message, Throwable cause) {
        super(message, cause);
    }
}
