package com.bank.account.domain.service;

import com.bank.account.domain.entity.Movement;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for Movement domain operations.
 * Defines the contract for movement-related business logic.
 */
public interface MovementService {

    /**
     * Creates a deposit movement and returns the saved entity.
     *
     * @param accountNumber Account number
     * @param amount Deposit amount
     * @param balanceAfter Balance after deposit
     * @return Created movement
     */
    Movement createDeposit(String accountNumber, java.math.BigDecimal amount, java.math.BigDecimal balanceAfter);

    /**
     * Creates a withdrawal movement and returns the saved entity.
     *
     * @param accountNumber Account number
     * @param amount Withdrawal amount
     * @param balanceAfter Balance after withdrawal
     * @return Created movement
     */
    Movement createWithdrawal(String accountNumber, java.math.BigDecimal amount, java.math.BigDecimal balanceAfter);

    /**
     * Finds a movement by ID.
     *
     * @param id Movement ID
     * @return Movement entity
     * @throws com.bank.account.domain.exception.AccountNotFoundException if not found
     */
    Movement getById(Long id);

    /**
     * Finds all movements.
     *
     * @return List of movements
     */
    List<Movement> findAll();

    /**
     * Finds movements by account number.
     *
     * @param accountNumber Account number
     * @return List of movements
     */
    List<Movement> findByAccountNumber(String accountNumber);

    /**
     * Finds movements for a client (for F4 report).
     *
     * @param clientId Client ID
     * @return List of movements
     */
    List<Movement> findByClientId(Long clientId);

    /**
     * Finds movements in a date range (for F4 report).
     *
     * @param startDate Start date
     * @param endDate End date
     * @return List of movements
     */
    List<Movement> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds movements for an account in a date range (for F4 report).
     *
     * @param accountNumber Account number
     * @param startDate Start date
     * @param endDate End date
     * @return List of movements
     */
    List<Movement> findByAccountAndDateRange(String accountNumber, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Deletes a movement.
     *
     * @param id Movement ID
     */
    void deleteMovement(Long id);
}
