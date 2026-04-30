package com.bank.account.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

/**
 * Domain event received from ms-customer via RabbitMQ.
 * Synchronized with ms-customer fields to prevent UnrecognizedPropertyException.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
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

    /**
     * Constructor Jackson-friendly.
     */
    @JsonCreator
    public static ClientEvent of(
            @JsonProperty("eventId")        String eventId,
            @JsonProperty("eventType")      String eventType,
            @JsonProperty("occurredOn")     Instant occurredOn,
            @JsonProperty("source")         String source,
            @JsonProperty("clientId")       Long clientId,
            @JsonProperty("identification") String identification,
            @JsonProperty("name")           String name,
            @JsonProperty("phone")          String phone,
            @JsonProperty("address")        String address,
            @JsonProperty("active")         Boolean active) {
        return new ClientEvent(
                eventId != null ? eventId : "unknown",
                eventType != null ? eventType : "UNKNOWN",
                occurredOn != null ? occurredOn : Instant.now(),
                source != null ? source : "ms-customer",
                clientId != null ? clientId : 0L,
                identification != null ? identification : "",
                name,
                phone,
                address,
                active != null ? active : true
        );
    }

    // -------------------------------------------------------------------------
    // Helper methods
    // -------------------------------------------------------------------------

    public boolean isClientCreated()     { return "CLIENT_CREATED".equals(eventType);     }
    public boolean isClientUpdated()     { return "CLIENT_UPDATED".equals(eventType);     }
    public boolean isClientDeleted()     { return "CLIENT_DELETED".equals(eventType);     }
    public boolean isClientActivated()   { return "CLIENT_ACTIVATED".equals(eventType);   }
    public boolean isClientDeactivated() { return "CLIENT_DEACTIVATED".equals(eventType); }

    // -------------------------------------------------------------------------
    // DomainEvent interface
    // -------------------------------------------------------------------------

    @Override public String  eventId()    { return eventId;    }
    @Override public String  eventType()  { return eventType;  }
    @Override public Instant occurredOn() { return occurredOn; }
    @Override public String  source()     { return source;     }
}
