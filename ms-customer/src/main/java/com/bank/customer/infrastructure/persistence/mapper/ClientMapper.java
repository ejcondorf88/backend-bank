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

        setField(client, "id", jpaEntity.getId());
        setField(client, "clientId", jpaEntity.getClientId());

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
        jpaEntity.setClientId(domain.getClientId());
        jpaEntity.setPassword(domain.getPassword());
        jpaEntity.setActive(domain.isActive());

        return jpaEntity;
    }

    private void setField(Client client, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = Client.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(client, value);
        } catch (Exception e) {
        }
    }
}