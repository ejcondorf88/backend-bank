package com.bank.customer.application.port.in.command;

/**
 * Comando para actualizar un cliente existente.
 * <p>
 * Incluye el ID del cliente a actualizar más los datos modificados.
 * La validación se delega al constructor de Client en el service.
 */
public record UpdateClientCommand(
        Long id,
        String name,
        String gender,
        Integer age,
        String identification,
        String address,
        String phone,
        String password,
        Boolean active
) {
    public UpdateClientCommand {
        // No hacemos validación aquí — se delega al constructor de Client
        // que ya valida name, age, password. active se normaliza a true si es null.
    }
}