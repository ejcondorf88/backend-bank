package com.bank.customer.infrastructure.persistence.mapper;

import com.bank.customer.domain.entity.Client;
import com.bank.customer.infrastructure.persistence.entity.ClientJpaEntity;
import com.bank.customer.infrastructure.persistence.entity.PersonJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper para convertir entre Client (dominio) y ClientJpaEntity (infraestructura).
 * <p>
 * ClientJpaEntity tiene una relacion @OneToOne con PersonJpaEntity.
 * El mapper aplana/aplana los campos de PersonJpaEntity para mapear a Client (dominio).
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ClientJpaMapper {

    /**
     * Convierte ClientJpaEntity (con Person anidada) a Client (dominio).
     * Los campos de Person (name, gender, age, etc.) vienen de person.getName(), etc.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "person.name")
    @Mapping(target = "gender", source = "person.gender")
    @Mapping(target = "age", source = "person.age")
    @Mapping(target = "identification", source = "person.identification")
    @Mapping(target = "address", source = "person.address")
    @Mapping(target = "phone", source = "person.phone")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "active", source = "active")
    Client toDomain(ClientJpaEntity jpaEntity);

    /**
     * Convierte Client (dominio) a ClientJpaEntity (con Person anidada).
     * Crea una PersonJpaEntity con los datos personales y la asigna al ClientJpaEntity.
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "person", source = ".", qualifiedByName = "toPersonJpa")
    @Mapping(target = "password", source = "password")
    @Mapping(target = "active", source = "active")
    ClientJpaEntity toJpaEntity(Client domain);

    @Named("toPersonJpa")
    default PersonJpaEntity mapPerson(Client client) {
        if (client == null) return null;
        PersonJpaEntity person = new PersonJpaEntity();
        person.setId(client.getId());
        person.setName(client.getName());
        person.setGender(client.getGender());
        person.setAge(client.getAge());
        person.setIdentification(client.getIdentification());
        person.setAddress(client.getAddress());
        person.setPhone(client.getPhone());
        return person;
    }
}