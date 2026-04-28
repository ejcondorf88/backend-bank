package com.bank.customer.infrastructure.persistence.repository;

import com.bank.customer.infrastructure.persistence.entity.ClientJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientJpaRepository extends JpaRepository<ClientJpaEntity, Long> {

    Optional<ClientJpaEntity> findByIdentification(String identification);

    boolean existsByIdentification(String identification);

    List<ClientJpaEntity> findByActiveTrue();

}
