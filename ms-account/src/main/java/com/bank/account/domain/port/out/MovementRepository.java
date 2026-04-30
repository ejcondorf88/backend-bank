package com.bank.account.domain.port.out;

import com.bank.account.domain.entity.Movement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovementRepository {

    Movement save(Movement movement);

    Optional<Movement> findById(Long id);

    List<Movement> findAll();

    List<Movement> findByAccountNumber(String accountNumber);

    List<Movement> findByAccountNumberAndDateBetween(String accountNumber,
                                                     LocalDateTime startDate,
                                                     LocalDateTime endDate);

    List<Movement> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<Movement> findByClientId(Long clientId);

    void deleteById(Long id);

    long countByAccountNumber(String accountNumber);
}