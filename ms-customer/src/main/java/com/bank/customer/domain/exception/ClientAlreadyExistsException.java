package com.bank.customer.domain.exception;

public class ClientAlreadyExistsException extends RuntimeException {

    public ClientAlreadyExistsException(String identification) {
        super("Client already exists with identification: " + identification);
    }

}
