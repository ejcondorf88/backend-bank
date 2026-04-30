package com.bank.customer.domain.event;

import com.bank.customer.domain.entity.Client;

import java.time.Instant;
import java.util.UUID;

/**
 * Evento de dominio relacionado con Clientes.
 * Usa record para garantizar inmutabilidad.
 * <p>
 * Todos los datos relevantes del cliente están como campos planos del record.
 * Esto elimina la duplicación que existía anteriormente entre los campos
 * del record y el ClientEventPayload.
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
        String phone,
        String address,
        Boolean active
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
    // Factory methods — parámetros individuales
    // -------------------------------------------------------------------------

    public static ClientEvent clientCreated(Long clientId, String identification,
                                            String name, String phone, String address,
                                            Boolean active) {
        return new ClientEvent(
                generateId(),
                EventType.CLIENT_CREATED.name(),
                Instant.now(),
                SERVICE_NAME,
                clientId,
                identification,
                name,
                phone,
                address,
                active
        );
    }

    public static ClientEvent clientUpdated(Long clientId, String identification,
                                            String name, String phone, String address,
                                            Boolean active) {
        return new ClientEvent(
                generateId(),
                EventType.CLIENT_UPDATED.name(),
                Instant.now(),
                SERVICE_NAME,
                clientId,
                identification,
                name,
                phone,
                address,
                active
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
                null,   // name
                null,   // phone
                null,   // address
                null    // active
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
                null,   // phone no aplica
                null,   // address no aplica
                Boolean.TRUE
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
                null,   // phone no aplica
                null,   // address no aplica
                Boolean.FALSE
        );
    }

    // -------------------------------------------------------------------------
    // Factory methods — desde entidad Client
    // -------------------------------------------------------------------------

    /**
     * Crea un evento CLIENT_CREATED a partir de un Client ya persistido.
     */
    public static ClientEvent fromCreated(Client client) {
        return clientCreated(
                client.getId(),
                client.getIdentification(),
                client.getName(),
                client.getPhone(),
                client.getAddress(),
                client.isActive()
        );
    }

    /**
     * Crea un evento CLIENT_UPDATED a partir de un Client ya persistido.
     */
    public static ClientEvent fromUpdated(Client client) {
        return clientUpdated(
                client.getId(),
                client.getIdentification(),
                client.getName(),
                client.getPhone(),
                client.getAddress(),
                client.isActive()
        );
    }

    /**
     * Crea un evento CLIENT_DELETED a partir de un Client.
     */
    public static ClientEvent fromDeleted(Client client) {
        return clientDeleted(client.getId(), client.getIdentification());
    }

    /**
     * Crea un evento CLIENT_ACTIVATED a partir de un Client.
     */
    public static ClientEvent fromActivated(Client client) {
        return clientActivated(
                client.getId(),
                client.getIdentification(),
                client.getName()
        );
    }

    /**
     * Crea un evento CLIENT_DEACTIVATED a partir de un Client.
     */
    public static ClientEvent fromDeactivated(Client client) {
        return clientDeactivated(
                client.getId(),
                client.getIdentification(),
                client.getName()
        );
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------

    private static String generateId() {
        return UUID.randomUUID().toString();
    }
}