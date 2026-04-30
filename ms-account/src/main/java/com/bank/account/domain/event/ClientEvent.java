package com.bank.account.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

/**
 * Evento de dominio recibido desde ms-customer via RabbitMQ.
 * Replica la estructura del evento publicado por ms-customer.
 *
 * <p>Usa @JsonCreator para que Jackson pueda deserializar el record
 * desde JSON sin depender del canonical constructor que tiene validaciones
 * estrictas que fallarían durante la deserialización parcial.
 *
 * <p>Tipos de evento:
 * <ul>
 *   <li>CLIENT_CREATED     — Nuevo cliente registrado</li>
 *   <li>CLIENT_UPDATED     — Datos del cliente modificados</li>
 *   <li>CLIENT_DELETED     — Cliente eliminado del sistema</li>
 *   <li>CLIENT_ACTIVATED   — Cliente habilitado</li>
 *   <li>CLIENT_DEACTIVATED — Cliente deshabilitado</li>
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
     * Constructor Jackson-friendly.
     * Sin validaciones — la validación queda en el dominio de ms-customer
     * que publicó el evento. ms-account solo consume y registra.
     */
    @JsonCreator
    public static ClientEvent of(
            @JsonProperty("eventId")       String eventId,
            @JsonProperty("eventType")     String eventType,
            @JsonProperty("occurredOn")    Instant occurredOn,
            @JsonProperty("source")        String source,
            @JsonProperty("clientId")      Long clientId,
            @JsonProperty("identification") String identification,
            @JsonProperty("name")          String name,
            @JsonProperty("active")        Boolean active,
            @JsonProperty("payload")       ClientEventPayload payload) {
        return new ClientEvent(
                eventId != null ? eventId : "unknown",
                eventType != null ? eventType : "UNKNOWN",
                occurredOn != null ? occurredOn : Instant.now(),
                source != null ? source : "ms-customer",
                clientId != null ? clientId : 0L,
                identification != null ? identification : "",
                name,
                active,
                payload
        );
    }

    // -------------------------------------------------------------------------
    // Helper methods — verificadores por tipo de evento
    // -------------------------------------------------------------------------

    public boolean isClientCreated()     { return "CLIENT_CREATED".equals(eventType);     }
    public boolean isClientUpdated()     { return "CLIENT_UPDATED".equals(eventType);     }
    public boolean isClientDeleted()     { return "CLIENT_DELETED".equals(eventType);     }
    public boolean isClientActivated()   { return "CLIENT_ACTIVATED".equals(eventType);   }
    public boolean isClientDeactivated() { return "CLIENT_DEACTIVATED".equals(eventType); }

    /**
     * Obtiene el estado activo del cliente desde payload o campo directo.
     */
    public boolean isActive() {
        if (payload != null && payload.active() != null) {
            return payload.active();
        }
        return active != null && active;
    }

    // -------------------------------------------------------------------------
    // DomainEvent interface
    // -------------------------------------------------------------------------

    @Override public String  eventId()    { return eventId;    }
    @Override public String  eventType()  { return eventType;  }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String  source()     { return source;     }
}
