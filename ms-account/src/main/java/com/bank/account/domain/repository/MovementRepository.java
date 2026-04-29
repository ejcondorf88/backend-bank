package com.bank.account.domain.repository;

import com.bank.account.domain.entity.Movement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository port for Movement entity.
 * This is part of the domain layer - the interface is defined here,
 * implementation belongs to the infrastructure layer.
 */
public interface MovementRepository {

    /**
     * Saves a movement (create or update).
     *
     * @param movement The movement to save
     * @return The saved movement
     */
    Movement save(Movement movement);

    /**
     * Finds a movement by its ID.
     *
     * @param id The movement ID
     * @return Optional containing the movement if found
     */
    Optional<Movement> findById(Long id);

    /**
     * Finds all movements.
     *
     * @return List of all movements
     */
    List<Movement> findAll();

    /**
     * Finds all movements for a specific account.
     *
     * @param accountNumber The account number
     * @return List of movements for the account
     */
    List<Movement> findByAccountNumber(String accountNumber);

    /**
     * Finds movements for an account within a date range.
     * Implements F4 requirement: report by date range.
     *
     * @param accountNumber The account number
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of movements in the date range
     */
    List<Movement> findByAccountNumberAndDateBetween(String accountNumber,
                                                     LocalDateTime startDate,
                                                     LocalDateTime endDate);

    /**
     * Finds movements within a date range (for report F4).
     *
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of movements in the date range
     */
    List<Movement> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds movements for a specific client (via account).
     * For F4 report by client.
     *
     * @param clientId The client ID
     * @return List of movements for the client's accounts
     */
    List<Movement> findByClientId(Long clientId);

    /**
     * Deletes a movement by its ID.
     *
     * @param id The movement ID to delete
     */
    void deleteById(Long id);

    /**
     * Counts movements for an account.
     *
     * @param accountNumber The account number
     * @return Number of movements
     */
    long countByAccountNumber(String accountNumber);
}
