package com.bank.account.infrastructure.persistence.repository;

import com.bank.account.infrastructure.persistence.entity.MovementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovementJpaRepository extends JpaRepository<MovementJpaEntity, Long> {

    List<MovementJpaEntity> findByAccountNumber(String accountNumber);

    List<MovementJpaEntity> findByAccountNumberOrderByDateDesc(String accountNumber);

    List<MovementJpaEntity> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<MovementJpaEntity> findByAccountNumberAndDateBetween(String accountNumber,
                                                               LocalDateTime startDate,
                                                               LocalDateTime endDate);

    long countByAccountNumber(String accountNumber);

    // F4: Find movements by client ID (join with accounts)
    @Query("SELECT m FROM MovementJpaEntity m WHERE m.accountNumber IN " +
           "(SELECT a.accountNumber FROM AccountJpaEntity a WHERE a.clientId = :clientId)")
    List<MovementJpaEntity> findByClientId(@Param("clientId") Long clientId);

    // F4: Find movements by client and date range
    @Query("SELECT m FROM MovementJpaEntity m WHERE m.accountNumber IN " +
           "(SELECT a.accountNumber FROM AccountJpaEntity a WHERE a.clientId = :clientId) " +
           "AND m.date BETWEEN :startDate AND :endDate")
    List<MovementJpaEntity> findByClientIdAndDateBetween(@Param("clientId") Long clientId,
                                                          @Param("startDate") LocalDateTime startDate,
                                                          @Param("endDate") LocalDateTime endDate);
}
