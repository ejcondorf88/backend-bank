package com.bank.account.domain.exception;

/**
 * Exception thrown when a client is not found in the local projection.
 * This is used to ensure referential integrity between microservices.
 */
public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(Long clientId) {
        super("Client not found with ID: " + clientId + ". Cannot create account for non-existing client.");
    }
}
