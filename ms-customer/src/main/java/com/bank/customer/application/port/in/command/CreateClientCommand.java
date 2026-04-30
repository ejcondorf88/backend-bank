package com.bank.customer.application.port.in.command;

/**
 * Comando para crear un nuevo cliente.
 * <p>
 * Encapsula los parámetros del caso de uso "Crear Cliente".
 * Al ser un record es inmutable por diseño. La validación se delega
 * al constructor del dominio (Client) que se invoca en el service.
 */
public record CreateClientCommand(
        String name,
        String gender,
        Integer age,
        String identification,
        String address,
        String phone,
        String password,
        Boolean active
) {
    public CreateClientCommand {
        // No hacemos validación aquí — se delega al constructor de Client
        // que ya valida name, age, password. active se normaliza a true si es null.
    }
}