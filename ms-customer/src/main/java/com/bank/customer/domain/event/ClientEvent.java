package com.bank.customer.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento de dominio relacionado con Clientes.
 * Usa record para garantizar inmutabilidad.
 *
 * <p>Tipos de evento manejados por {@link EventType}:
 * <ul>
 *   <li>CLIENT_CREATED     - Nuevo cliente registrado</li>
 *   <li>CLIENT_UPDATED     - Datos del cliente modificados</li>
 *   <li>CLIENT_DELETED     - Cliente eliminado del sistema</li>
 *   <li>CLIENT_ACTIVATED   - Cliente habilitado</li>
 *   <li>CLIENT_DEACTIVATED - Cliente deshabilitado</li>
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

    static final String SERVICE_NAME = "ms-customer";

    // Compact constructor — normaliza source antes de validar
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
        source = (source == null || source.isBlank()) ? SERVICE_NAME : source;

        // NPE separado del check de valor negativo
        if (clientId == null) {
            throw new IllegalArgumentException("clientId cannot be null");
        }
        if (clientId <= 0) {
            throw new IllegalArgumentException("clientId must be a positive number");
        }
        if (identification == null || identification.isBlank()) {
            throw new IllegalArgumentException("identification cannot be null or blank");
        }
    }

    // -------------------------------------------------------------------------
    // Factory methods
    // -------------------------------------------------------------------------

    public static ClientEvent clientCreated(Long clientId, String identification,
                                            String name, Boolean active,
                                            ClientEventPayload payload) {
        return new ClientEvent(
                generateId(),
                EventType.CLIENT_CREATED.name(),
                Instant.now(),
                SERVICE_NAME,
                clientId,
                identification,
                name,
                active,
                payload
        );
    }

    public static ClientEvent clientUpdated(Long clientId, String identification,
                                            String name, Boolean active,
                                            ClientEventPayload payload) {
        return new ClientEvent(
                generateId(),
                EventType.CLIENT_UPDATED.name(),
                Instant.now(),
                SERVICE_NAME,
                clientId,
                identification,
                name,
                active,
                payload
        );
    }

    public static ClientEvent clientDeleted(Long clientId, String identification) {
        return new ClientEvent(
                generateId(),
                EventType.CLIENT_DELETED.name(),
                Instant.now(),
                SERVICE_NAME,
                clientId,
                identification,
                null,   // name no aplica para eliminación
                null,   // active no aplica para eliminación
                null    // payload no aplica para eliminación
        );
    }

    public static ClientEvent clientActivated(Long clientId, String identification, String name) {
        return new ClientEvent(
                generateId(),
                EventType.CLIENT_ACTIVATED.name(),
                Instant.now(),
                SERVICE_NAME,
                clientId,
                identification,
                name,
                Boolean.TRUE,
                null
        );
    }

    public static ClientEvent clientDeactivated(Long clientId, String identification, String name) {
        return new ClientEvent(
                generateId(),
                EventType.CLIENT_DEACTIVATED.name(),
                Instant.now(),
                SERVICE_NAME,
                clientId,
                identification,
                name,
                Boolean.FALSE,
                null
        );
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------

    private static String generateId() {
        return UUID.randomUUID().toString();
    }
}
