package com.bank.account.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento de dominio recibido desde el microservicio de clientes.
 * Replica la estructura del evento publicado por ms-customer.
 * Permite mantener consistencia eventual entre servicios.
 *
 * <p>Tipos de evento:
 * <ul>
 * <li>CLIENT_CREATED - Nuevo cliente registrado</li>
 * <li>CLIENT_UPDATED - Datos del cliente modificados</li>
 * <li>CLIENT_DELETED - Cliente eliminado del sistema</li>
 * <li>CLIENT_ACTIVATED - Cliente habilitado</li>
 * <li>CLIENT_DEACTIVATED - Cliente deshabilitado</li>
 * </ul>
 */
public record ClientEvent(
        String eventId,
        String eventType,
        Instant occurredOn,
        String source,
        Long clientId,
        String identification,
        String name,
        Boolean active,
        ClientEventPayload payload
) implements DomainEvent {

    /**
     * Constructor compacto con validaciones.
     */
    public ClientEvent {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("eventId cannot be null or blank");
        }
        if (eventType == null || eventType.isBlank()) {
            throw new IllegalArgumentException("eventType cannot be null or blank");
        }
        if (occurredOn == null) {
            throw new IllegalArgumentException("occurredOn cannot be null");
        }
        if (source == null || source.isBlank()) {
            source = "ms-customer";
        }
        if (clientId == null || clientId <= 0) {
            throw new IllegalArgumentException("clientId must be a positive number");
        }
        if (identification == null || identification.isBlank()) {
            throw new IllegalArgumentException("identification cannot be null or blank");
        }
    }

    // -------------------------------------------------------------------------
    // Helper methods
    // -------------------------------------------------------------------------

    /**
     * Verifica si es un evento de cliente creado.
     */
    public boolean isClientCreated() {
        return "CLIENT_CREATED".equals(eventType);
    }

    /**
     * Verifica si es un evento de cliente actualizado.
     */
    public boolean isClientUpdated() {
        return "CLIENT_UPDATED".equals(eventType);
    }

    /**
     * Verifica si es un evento de cliente eliminado.
     */
    public boolean isClientDeleted() {
        return "CLIENT_DELETED".equals(eventType);
    }

    /**
     * Verifica si es un evento de cliente activado.
     */
    public boolean isClientActivated() {
        return "CLIENT_ACTIVATED".equals(eventType);
    }

    /**
     * Verifica si es un evento de cliente desactivado.
     */
    public boolean isClientDeactivated() {
        return "CLIENT_DEACTIVATED".equals(eventType);
    }

    /**
     * Obtiene el estado activo del cliente del payload o del campo directo.
     */
    public boolean isActive() {
        if (payload != null && payload.active() != null) {
            return payload.active();
        }
        return active != null && active;
    }

    @Override
    public String eventId() {
        return eventId;
    }

    @Override
    public String eventType() {
        return eventType;
    }

    @Override
    public Instant occurredOn() {
        return occurredOn;
    }

    @Override
    public String source() {
        return source;
    }
}
