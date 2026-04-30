package com.bank.account.domain.port.in;

import com.bank.account.domain.entity.Movement;

import java.time.LocalDateTime;
import java.util.List;

public interface MovementService {

    Movement createDeposit(String accountNumber, java.math.BigDecimal amount, java.math.BigDecimal balanceAfter);

    Movement createWithdrawal(String accountNumber, java.math.BigDecimal amount, java.math.BigDecimal balanceAfter);

    Movement getById(Long id);

    List<Movement> findAll();

    List<Movement> findByAccountNumber(String accountNumber);

    List<Movement> findByClientId(Long clientId);

    List<Movement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<Movement> findByAccountAndDateRange(String accountNumber, LocalDateTime startDate, LocalDateTime endDate);

    void deleteMovement(Long id);
}