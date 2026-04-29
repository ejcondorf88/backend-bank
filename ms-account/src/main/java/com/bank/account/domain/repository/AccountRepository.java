package com.bank.account.domain.repository;

import com.bank.account.domain.entity.Account;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for Account entity.
 * This is part of the domain layer - the interface is defined here,
 * implementation belongs to the infrastructure layer.
 */
public interface AccountRepository {

    /**
     * Saves an account (create or update).
     *
     * @param account The account to save
     * @return The saved account
     */
    Account save(Account account);

    /**
     * Finds an account by its ID.
     *
     * @param id The account ID
     * @return Optional containing the account if found
     */
    Optional<Account> findById(Long id);

    /**
     * Finds an account by its account number.
     *
     * @param accountNumber The unique account number
     * @return Optional containing the account if found
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Finds all accounts.
     *
     * @return List of all accounts
     */
    List<Account> findAll();

    /**
     * Finds all accounts belonging to a specific client.
     *
     * @param clientId The client ID
     * @return List of accounts for the client
     */
    List<Account> findByClientId(Long clientId);

    /**
     * Finds all active accounts.
     *
     * @return List of active accounts
     */
    List<Account> findByActiveTrue();

    /**
     * Deletes an account by its ID.
     *
     * @param id The account ID to delete
     */
    void deleteById(Long id);

    /**
     * Checks if an account exists with the given account number.
     *
     * @param accountNumber The account number to check
     * @return true if exists, false otherwise
     */
    boolean existsByAccountNumber(String accountNumber);
}
