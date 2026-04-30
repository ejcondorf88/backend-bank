package com.bank.account.domain.event;

/**
 * Payload del evento de cliente recibido desde ms-customer.
 * Contiene los datos completos del cliente para sincronización.
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
     * Factory method para crear payload desde parámetros.
     */
    public static ClientEventPayload from(
            Long id, String name, String identification,
            String phone, String address, Boolean active) {
        return new ClientEventPayload(id, name, identification, phone, address, active);
    }
}
