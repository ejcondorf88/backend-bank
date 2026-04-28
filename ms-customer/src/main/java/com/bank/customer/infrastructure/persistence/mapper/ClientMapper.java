package com.bank.customer.infrastructure.persistence.mapper;

import com.bank.customer.domain.entity.Client;
import com.bank.customer.infrastructure.persistence.entity.ClientJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public Client toDomain(ClientJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        Client client = new Client(
            jpaEntity.getName(),
            jpaEntity.getGender(),
            jpaEntity.getAge(),
            jpaEntity.getIdentification(),
            jpaEntity.getAddress(),
            jpaEntity.getPhone(),
            jpaEntity.getPassword(),
            jpaEntity.isActive()
        );

        // Usar el método protegido setId heredado de Person
        client.setId(jpaEntity.getId());

        return client;
    }

    public ClientJpaEntity toJpa(Client domain) {
        if (domain == null) {
            return null;
        }

        ClientJpaEntity jpaEntity = new ClientJpaEntity();
        jpaEntity.setId(domain.getId());
        jpaEntity.setName(domain.getName());
        jpaEntity.setGender(domain.getGender());
        jpaEntity.setAge(domain.getAge());
        jpaEntity.setIdentification(domain.getIdentification());
        jpaEntity.setAddress(domain.getAddress());
        jpaEntity.setPhone(domain.getPhone());
        jpaEntity.setPassword(domain.getPassword());
        jpaEntity.setActive(domain.isActive());

        return jpaEntity;
    }
}