package com.bank.customer.infrastructure.persistence.mapper;

import com.bank.customer.domain.entity.Client;
import com.bank.customer.infrastructure.persistence.entity.ClientJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * MapStruct mapper para convertir entre Client (dominio) y ClientJpaEntity (infraestructura).
 * Pertenece a la capa de infraestructura.
 */
@Mapper(componentModel = "spring")
public interface ClientJpaMapper {

    /**
     * Convierte una entidad JPA a dominio.
     * El ID se mapea automáticamente del campo id heredado.
     */
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "name", target = "name"),
        @Mapping(source = "gender", target = "gender"),
        @Mapping(source = "age", target = "age"),
        @Mapping(source = "identification", target = "identification"),
        @Mapping(source = "address", target = "address"),
        @Mapping(source = "phone", target = "phone"),
        @Mapping(source = "password", target = "password"),
        @Mapping(source = "active", target = "active")
    })
    Client toDomain(ClientJpaEntity jpaEntity);

    /**
     * Convierte una entidad de dominio a JPA.
     * Los campos protegidos se acceden mediante los getters públicos.
     */
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "name", target = "name"),
        @Mapping(source = "gender", target = "gender"),
        @Mapping(source = "age", target = "age"),
        @Mapping(source = "identification", target = "identification"),
        @Mapping(source = "address", target = "address"),
        @Mapping(source = "phone", target = "phone"),
        @Mapping(source = "password", target = "password"),
        @Mapping(source = "active", target = "active")
    })
    ClientJpaEntity toJpaEntity(Client domain);
}
