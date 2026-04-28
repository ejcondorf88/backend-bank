package com.bank.customer.domain.exception;

public class InvalidClientStateException extends RuntimeException {

    public InvalidClientStateException(String message) {
        super(message);
    }

}
