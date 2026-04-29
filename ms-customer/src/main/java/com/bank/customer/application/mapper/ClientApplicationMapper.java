package com.bank.customer.application.mapper;

import com.bank.customer.application.dto.ClientRequestDto;
import com.bank.customer.application.dto.ClientResponseDto;
import com.bank.customer.domain.entity.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper para convertir entre DTOs y Client (dominio).
 * Pertenece a la capa de aplicación.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ClientApplicationMapper {

    /**
     * Convierte un DTO de request a entidad de dominio.
     * Usa un factory method para construir el Client con validaciones.
     * El defaultValue de MapStruct NO funciona con constructores (solo con setters),
     * por eso este método es default y maneja el null de active explícitamente.
     */
    default Client toDomain(ClientRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return new Client(
            dto.getName(),
            dto.getGender(),
            dto.getAge(),
            dto.getIdentification(),
            dto.getAddress(),
            dto.getPhone(),
            dto.getPassword(),
            dto.getActive() != null ? dto.getActive() : true
        );
    }

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
