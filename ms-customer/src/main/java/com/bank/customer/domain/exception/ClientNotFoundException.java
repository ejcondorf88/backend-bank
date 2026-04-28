package com.bank.customer.domain.exception;
public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(Long id) {
        super("Client not found with id: " + id);
    }

    public ClientNotFoundException(String identification) {
        super("Client not found with identification: " + identification);
    }

}
