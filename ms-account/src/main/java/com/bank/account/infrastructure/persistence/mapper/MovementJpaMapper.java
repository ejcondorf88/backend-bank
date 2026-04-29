package com.bank.account.infrastructure.persistence.mapper;

import com.bank.account.domain.entity.Movement;
import com.bank.account.infrastructure.persistence.entity.MovementJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class MovementJpaMapper {

    /**
     * Maps a JPA entity to a domain entity.
     *
     * @param jpaEntity the JPA entity
     * @return the domain entity
     */
    public Movement toDomain(MovementJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        // Create domain entity using constructor
        Movement movement = new Movement(
                jpaEntity.getAccountNumber(),
                jpaEntity.getDate(),
                jpaEntity.getType(),
                jpaEntity.getAmount(),
                jpaEntity.getBalance()
        );

        // Set the ID
        movement.setId(jpaEntity.getId());

        return movement;
    }

    /**
     * Maps a domain entity to a JPA entity.
     *
     * @param domain the domain entity
     * @return the JPA entity
     */
    public MovementJpaEntity toJpaEntity(Movement domain) {
        if (domain == null) {
            return null;
        }

        MovementJpaEntity jpaEntity = new MovementJpaEntity();
        jpaEntity.setId(domain.getId());
        jpaEntity.setAccountNumber(domain.getAccountNumber());
        jpaEntity.setDate(domain.getDate());
        jpaEntity.setType(domain.getType());
        jpaEntity.setAmount(domain.getAmount());
        jpaEntity.setBalance(domain.getBalance());

        return jpaEntity;
    }
}
