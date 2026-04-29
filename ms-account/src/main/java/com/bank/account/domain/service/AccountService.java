package com.bank.account.domain.service;

import com.bank.account.domain.entity.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for account operations.
 * Defines use cases for the account domain using domain entities.
 */
public interface AccountService {

    /**
     * Creates a new account.
     *
     * @param account the account to create
     * @return the created account
     */
    Account createAccount(Account account);

    /**
     * Finds an account by its account number.
     *
     * @param accountNumber the unique account number
     * @return optional containing the account if found
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Finds an account by its ID.
     *
     * @param id the account ID
     * @return optional containing the account if found
     */
    Optional<Account> findById(Long id);

    /**
     * Returns all accounts.
     *
     * @return list of all accounts
     */
    List<Account> findAll();

    /**
     * Returns all accounts for a specific client.
     *
     * @param clientId the client ID
     * @return list of client accounts
     */
    List<Account> findByClientId(Long clientId);

    /**
     * Updates an existing account.
     *
     * @param account the account to update
     * @return the updated account
     */
    Account updateAccount(Account account);

    /**
     * Deletes an account by its ID.
     *
     * @param id the account ID to delete
     */
    void deleteAccount(Long id);

    /**
     * Activates an account.
     *
     * @param id the account ID to activate
     * @return the updated account
     */
    Account activateAccount(Long id);

    /**
     * Deactivates an account.
     *
     * @param id the account ID to deactivate
     * @return the updated account
     */
    Account deactivateAccount(Long id);

    /**
     * Deposits money into an account.
     *
     * @param accountNumber the account number
     * @param amount the amount to deposit
     * @return the updated account
     */
    Account deposit(String accountNumber, BigDecimal amount);

    /**
     * Withdraws money from an account.
     *
     * @param accountNumber the account number
     * @param amount the amount to withdraw
     * @return the updated account
     */
    Account withdraw(String accountNumber, BigDecimal amount);

    /**
     * Checks if an account exists with the given account number.
     *
     * @param accountNumber the account number to check
     * @return true if exists, false otherwise
     */
    boolean existsByAccountNumber(String accountNumber);
}
