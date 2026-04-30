package com.bank.customer.infrastructure.persistence.repository;

import com.bank.customer.infrastructure.persistence.entity.ClientJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientJpaRepository extends JpaRepository<ClientJpaEntity, Long> {

    @Query("SELECT c FROM ClientJpaEntity c JOIN c.person p WHERE p.identification = :identification")
    Optional<ClientJpaEntity> findByIdentification(String identification);

    @Query("SELECT COUNT(c) > 0 FROM ClientJpaEntity c JOIN c.person p WHERE p.identification = :identification")
    boolean existsByIdentification(String identification);

    List<ClientJpaEntity> findByActiveTrue();

}
