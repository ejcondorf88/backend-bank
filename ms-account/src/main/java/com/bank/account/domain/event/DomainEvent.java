package com.bank.account.domain.event;

import java.time.Instant;

/**
 * Interfaz base para todos los eventos de dominio.
 * Define el contrato mínimo que todo evento debe cumplir.
 */
public interface DomainEvent {

    /**
     * Obtiene el ID unico del evento.
     */
    String eventId();

    /**
     * Obtiene el tipo de evento (ej: "CLIENT_CREATED", "CLIENT_UPDATED").
     */
    String eventType();

    /**
     * Obtiene el timestamp del evento.
     */
    Instant occurredOn();

    /**
     * Obtiene el nombre del servicio que origino el evento.
     */
    String source();
}
