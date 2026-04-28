package com.bank.customer.domain.event;

/**
 * Tipos de eventos de dominio del microservicio de clientes.
 * Usar {@code EventType.CLIENT_CREATED.name()} para obtener el String del tipo.
 */
public enum EventType {
    CLIENT_CREATED,
    CLIENT_UPDATED,
    CLIENT_DELETED,
    CLIENT_ACTIVATED,
    CLIENT_DEACTIVATED
}