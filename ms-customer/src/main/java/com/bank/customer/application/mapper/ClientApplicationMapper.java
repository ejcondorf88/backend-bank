package com.bank.customer.application.mapper;

import com.bank.customer.application.dto.ClientRequestDto;
import com.bank.customer.application.dto.ClientResponseDto;
import com.bank.customer.application.port.in.command.CreateClientCommand;
import com.bank.customer.application.port.in.command.UpdateClientCommand;
import com.bank.customer.domain.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper para convertir entre DTOs, Commands y Client (dominio).
 * Pertenece a la capa de aplicación.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ClientApplicationMapper {

    /**
     * Convierte un DTO de request a entidad de dominio.
     * Se usa principalmente para construir el CreateClientCommand en el controller.
     */
    default CreateClientCommand toCreateCommand(ClientRequestDto dto) {
        if (dto == null) return null;
        return new CreateClientCommand(
            dto.getName(),
            dto.getGender(),
            dto.getAge(),
            dto.getIdentification(),
            dto.getAddress(),
            dto.getPhone(),
            dto.getPassword(),
            dto.getActive()
        );
    }

    /**
     * Convierte un DTO de request + ID a UpdateClientCommand.
     */
    default UpdateClientCommand toUpdateCommand(Long id, ClientRequestDto dto) {
        if (dto == null) return null;
        return new UpdateClientCommand(
            id,
            dto.getName(),
            dto.getGender(),
            dto.getAge(),
            dto.getIdentification(),
            dto.getAddress(),
            dto.getPhone(),
            dto.getPassword(),
            dto.getActive()
        );
    }

    /**
     * Convierte un CreateClientCommand a entidad Client (dominio).
     * Construye el Client con validaciones del dominio.
     * active se normaliza a true si es null.
     */
    default Client toDomain(CreateClientCommand command) {
        if (command == null) return null;
        return new Client(
            command.name(),
            command.gender(),
            command.age(),
            command.identification(),
            command.address(),
            command.phone(),
            command.password(),
            command.active() != null ? command.active() : true
        );
    }

    /**
     * Convierte un UpdateClientCommand a entidad Client (dominio), incluyendo el ID.
     */
    default Client toDomain(UpdateClientCommand command) {
        if (command == null) return null;
        Client client = new Client(
            command.name(),
            command.gender(),
            command.age(),
            command.identification(),
            command.address(),
            command.phone(),
            command.password(),
            command.active() != null ? command.active() : true
        );
        client.setId(command.id());
        return client;
    }

    /**
     * Convierte un Client de dominio a DTO de respuesta.
     * No incluye la contraseña por seguridad.
     */
    default ClientResponseDto toResponseDto(Client client) {
        if (client == null) return null;
        return new ClientResponseDto(
            client.getId(),
            client.getName(),
            client.getGender(),
            client.getAge(),
            client.getIdentification(),
            client.getAddress(),
            client.getPhone(),
            client.isActive()
        );
    }
}