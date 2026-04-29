package com.bank.account.infrastructure.persistence.repository;

import com.bank.account.infrastructure.persistence.entity.AccountJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, Long> {

    Optional<AccountJpaEntity> findByAccountNumber(String accountNumber);

    boolean existsByAccountNumber(String accountNumber);

    List<AccountJpaEntity> findByClientId(Long clientId);

    List<AccountJpaEntity> findByActiveTrue();
}
