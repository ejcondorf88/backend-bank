package com.bank.customer.domain.event;

/**
 * Payload tipado para los eventos de Cliente.
 * Reemplaza Map<String, Object> para garantizar type-safety en la mensajería.
 * Al ser un record es inmutable por diseño.
 */
public record ClientEventPayload(
        Long id,
        String name,
        String identification,
        String phone,
        String address,
        Boolean active
) {
    /**
     * Factory method desde una entidad de dominio ya persistida.
     * Solo se llama después de que el repositorio asignó el ID.
     */
    public static ClientEventPayload from(
            Long id, String name, String identification,
            String phone, String address, Boolean active) {
        return new ClientEventPayload(id, name, identification, phone, address, active);
    }
}
