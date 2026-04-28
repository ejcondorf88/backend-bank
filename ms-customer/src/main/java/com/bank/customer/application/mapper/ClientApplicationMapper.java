package com.bank.customer.application.mapper;

import com.bank.customer.application.dto.ClientRequestDto;
import com.bank.customer.application.dto.ClientResponseDto;
import com.bank.customer.domain.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

/**
 * MapStruct mapper para convertir entre DTOs y Client (dominio).
 * Pertenece a la capa de aplicación.
 */
@Mapper(componentModel = "spring")
public interface ClientApplicationMapper {

    /**
     * Convierte un DTO de request a entidad de dominio.
     * Usa un factory method para construir el Client con validaciones.
     */
    @Mappings({
        @Mapping(target = "id", ignore = true), // El ID se genera en la base de datos
        @Mapping(source = "name", target = "name"),
        @Mapping(source = "gender", target = "gender"),
        @Mapping(source = "age", target = "age"),
        @Mapping(source = "identification", target = "identification"),
        @Mapping(source = "address", target = "address"),
        @Mapping(source = "phone", target = "phone"),
        @Mapping(source = "password", target = "password"),
        @Mapping(source = "active", target = "active", defaultValue = "true")
    })
    Client toDomain(ClientRequestDto dto);

    /**
     * Convierte un Client de dominio a DTO de respuesta.
     * No incluye la contraseña por seguridad.
     */
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "name", target = "name"),
        @Mapping(source = "gender", target = "gender"),
        @Mapping(source = "age", target = "age"),
        @Mapping(source = "identification", target = "identification"),
        @Mapping(source = "address", target = "address"),
        @Mapping(source = "phone", target = "phone"),
        @Mapping(source = "active", target = "active")
    })
    ClientResponseDto toResponseDto(Client client);

    /**
     * Crea un Client para actualización, incluyendo el ID existente.
     * MapStruct no puede setear campos protegidos directamente,
     * así que usamos un método por defecto.
     */
    default Client toDomainForUpdate(Long id, ClientRequestDto dto) {
        if (dto == null) {
            return null;
        }
        
        // Crear el cliente usando el constructor que valida
        Client client = new Client(
            dto.getName(),
            dto.getGender(),
            dto.getAge(),
            dto.getIdentification(),
            dto.getAddress(),
            dto.getPhone(),
            dto.getPassword(),
            dto.getActive() != null ? dto.getActive() : true
        );
        
        // Establecer el ID heredado de Person
        client.setId(id);
        
        return client;
    }
}
