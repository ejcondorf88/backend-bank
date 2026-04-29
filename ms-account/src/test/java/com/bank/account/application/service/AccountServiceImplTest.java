package com.bank.account.application.service;

import com.bank.account.domain.entity.Account;
import com.bank.account.domain.exception.AccountAlreadyExistsException;
import com.bank.account.domain.exception.AccountNotFoundException;
import com.bank.account.domain.exception.InsufficientBalanceException;
import com.bank.account.domain.exception.InvalidAccountStateException;
import com.bank.account.domain.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountServiceImpl.
 * Tests application layer use cases with mocked repository.
 * Tests the implementation of domain AccountService interface.
 * No Spring context - pure Mockito tests.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AccountServiceImpl Tests")
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account("478758", "Ahorro", new BigDecimal("2000.00"), true, 1L);
        // Use reflection to set ID since constructor doesn't set it
        account.setId(1L);
    }

    // ==================== Create Account Tests ====================

    @Test
    @DisplayName("Should create account successfully")
    void shouldCreateAccountSuccessfully() {
        // Given
        Account newAccount = new Account("478758", "Ahorro", new BigDecimal("2000.00"), true, 1L);
        when(accountRepository.existsByAccountNumber("478758")).thenReturn(false);
        when(accountRepository.save(newAccount)).thenReturn(account);

        // When
        Account result = accountService.createAccount(newAccount);

        // Then
        assertNotNull(result);
        assertEquals("478758", result.getAccountNumber());
        assertEquals("Ahorro", result.getAccountType());
        verify(accountRepository).existsByAccountNumber("478758");
        verify(accountRepository).save(newAccount);
    }

    @Test
    @DisplayName("Should throw exception when creating account with duplicate number")
    void shouldThrowExceptionWhenCreatingAccountWithDuplicateNumber() {
        // Given
        Account newAccount = new Account("478758", "Ahorro", new BigDecimal("2000.00"), true, 1L);
        when(accountRepository.existsByAccountNumber("478758")).thenReturn(true);

        // When & Then
        AccountAlreadyExistsException exception = assertThrows(AccountAlreadyExistsException.class, () ->
                accountService.createAccount(newAccount));
        assertEquals("Account already exists with number: 478758", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    // ==================== Find Account Tests ====================

    @Test
    @DisplayName("Should find account by account number")
    void shouldFindAccountByAccountNumber() {
        // Given
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));

        // When
        Optional<Account> result = accountService.findByAccountNumber("478758");

        // Then
        assertTrue(result.isPresent());
        assertEquals("478758", result.get().getAccountNumber());
        verify(accountRepository).findByAccountNumber("478758");
    }

    @Test
    @DisplayName("Should return empty optional when account not found by number")
    void shouldReturnEmptyOptionalWhenAccountNotFoundByNumber() {
        // Given
        when(accountRepository.findByAccountNumber("999999")).thenReturn(Optional.empty());

        // When
        Optional<Account> result = accountService.findByAccountNumber("999999");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should find account by id")
    void shouldFindAccountById() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // When
        Optional<Account> result = accountService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    @DisplayName("Should return empty optional when account not found by id")
    void shouldReturnEmptyOptionalWhenAccountNotFoundById() {
        // Given
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Account> result = accountService.findById(999L);

        // Then
        assertTrue(result.isEmpty());
    }

    // ==================== Find All Accounts Tests ====================

    @Test
    @DisplayName("Should return all accounts")
    void shouldReturnAllAccounts() {
        // Given
        Account account2 = new Account("225487", "Corriente", new BigDecimal("1000.00"), true, 2L);
        account2.setId(2L);
        List<Account> accounts = Arrays.asList(account, account2);

        when(accountRepository.findAll()).thenReturn(accounts);

        // When
        List<Account> result = accountService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("478758", result.get(0).getAccountNumber());
        assertEquals("225487", result.get(1).getAccountNumber());
    }

    @Test
    @DisplayName("Should return empty list when no accounts")
    void shouldReturnEmptyListWhenNoAccounts() {
        // Given
        when(accountRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Account> result = accountService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== Find By Client ID Tests ====================

    @Test
    @DisplayName("Should return accounts by client id")
    void shouldReturnAccountsByClientId() {
        // Given
        when(accountRepository.findByClientId(1L)).thenReturn(Arrays.asList(account));

        // When
        List<Account> result = accountService.findByClientId(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getClientId());
    }

    @Test
    @DisplayName("Should return empty list when client has no accounts")
    void shouldReturnEmptyListWhenClientHasNoAccounts() {
        // Given
        when(accountRepository.findByClientId(999L)).thenReturn(Collections.emptyList());

        // When
        List<Account> result = accountService.findByClientId(999L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== Update Account Tests ====================

    @Test
    @DisplayName("Should update account successfully")
    void shouldUpdateAccountSuccessfully() {
        // Given
        Account updateAccount = new Account("478758", "Corriente", new BigDecimal("3000.00"), true, 1L);
        updateAccount.setId(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(updateAccount)).thenReturn(updateAccount);

        // When
        Account result = accountService.updateAccount(updateAccount);

        // Then
        assertNotNull(result);
        verify(accountRepository).save(updateAccount);
    }

    @Test
    @DisplayName("Should throw exception when updating account without ID")
    void shouldThrowExceptionWhenUpdatingAccountWithoutId() {
        // Given
        Account updateAccount = new Account("478758", "Ahorro", new BigDecimal("2000.00"), true, 1L);
        // No ID set

        // When & Then
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () ->
                accountService.updateAccount(updateAccount));
        assertEquals("Account not found with id: null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing account")
    void shouldThrowExceptionWhenUpdatingNonExistingAccount() {
        // Given
        Account updateAccount = new Account("478758", "Ahorro", new BigDecimal("2000.00"), true, 1L);
        updateAccount.setId(999L);

        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () ->
                accountService.updateAccount(updateAccount));
        assertEquals("Account not found with id: 999", exception.getMessage());
    }

    // ==================== Delete Account Tests ====================

    @Test
    @DisplayName("Should delete account successfully")
    void shouldDeleteAccountSuccessfully() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        // When
        accountService.deleteAccount(1L);

        // Then
        verify(accountRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing account")
    void shouldThrowExceptionWhenDeletingNonExistingAccount() {
        // Given
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () ->
                accountService.deleteAccount(999L));
        assertEquals("Account not found with id: 999", exception.getMessage());
        verify(accountRepository, never()).deleteById(any());
    }

    // ==================== Activate Account Tests ====================

    @Test
    @DisplayName("Should activate account successfully")
    void shouldActivateAccountSuccessfully() {
        // Given
        Account inactiveAccount = new Account("478758", "Ahorro", new BigDecimal("2000.00"), false, 1L);
        inactiveAccount.setId(1L);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(inactiveAccount));
        when(accountRepository.save(inactiveAccount)).thenReturn(inactiveAccount);

        // When
        Account result = accountService.activateAccount(1L);

        // Then
        assertNotNull(result);
        assertTrue(result.isActive());
        verify(accountRepository).save(inactiveAccount);
    }

    @Test
    @DisplayName("Should throw exception when activating non-existing account")
    void shouldThrowExceptionWhenActivatingNonExistingAccount() {
        // Given
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () ->
                accountService.activateAccount(999L));
        assertEquals("Account not found with id: 999", exception.getMessage());
    }

    // ==================== Deactivate Account Tests ====================

    @Test
    @DisplayName("Should deactivate account successfully")
    void shouldDeactivateAccountSuccessfully() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        // When
        Account result = accountService.deactivateAccount(1L);

        // Then
        assertNotNull(result);
        assertFalse(result.isActive());
        verify(accountRepository).save(account);
    }

    @Test
    @DisplayName("Should throw exception when deactivating non-existing account")
    void shouldThrowExceptionWhenDeactivatingNonExistingAccount() {
        // Given
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () ->
                accountService.deactivateAccount(999L));
        assertEquals("Account not found with id: 999", exception.getMessage());
    }

    // ==================== Deposit Tests ====================

    @Test
    @DisplayName("Should deposit successfully")
    void shouldDepositSuccessfully() {
        // Given
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        // When
        Account result = accountService.deposit("478758", new BigDecimal("500.00"));

        // Then
        assertNotNull(result);
        assertEquals(0, new BigDecimal("2500.00").compareTo(result.getBalance()));
        verify(accountRepository).save(account);
    }

    @Test
    @DisplayName("Should throw exception when depositing to non-existing account")
    void shouldThrowExceptionWhenDepositingToNonExistingAccount() {
        // Given
        when(accountRepository.findByAccountNumber("999999")).thenReturn(Optional.empty());

        // When & Then
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () ->
                accountService.deposit("999999", new BigDecimal("500.00")));
        assertEquals("Account not found with number: 999999", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when depositing to inactive account")
    void shouldThrowExceptionWhenDepositingToInactiveAccount() {
        // Given
        Account inactiveAccount = new Account("478758", "Ahorro", new BigDecimal("2000.00"), false, 1L);
        inactiveAccount.setId(1L);

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(inactiveAccount));

        // When & Then
        InvalidAccountStateException exception = assertThrows(InvalidAccountStateException.class, () ->
                accountService.deposit("478758", new BigDecimal("500.00")));
        assertEquals("Cannot deposit to inactive account", exception.getMessage());
    }

    // ==================== Withdraw Tests ====================

    @Test
    @DisplayName("Should withdraw successfully")
    void shouldWithdrawSuccessfully() {
        // Given
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        // When
        Account result = accountService.withdraw("478758", new BigDecimal("500.00"));

        // Then
        assertNotNull(result);
        assertEquals(0, new BigDecimal("1500.00").compareTo(result.getBalance()));
        verify(accountRepository).save(account);
    }

    @Test
    @DisplayName("Should throw exception when withdrawing from non-existing account")
    void shouldThrowExceptionWhenWithdrawingFromNonExistingAccount() {
        // Given
        when(accountRepository.findByAccountNumber("999999")).thenReturn(Optional.empty());

        // When & Then
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () ->
                accountService.withdraw("999999", new BigDecimal("500.00")));
        assertEquals("Account not found with number: 999999", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when withdrawing with insufficient balance - F3")
    void shouldThrowExceptionWhenWithdrawingWithInsufficientBalance() {
        // Given
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));

        // When & Then - The domain entity throws InsufficientBalanceException
        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () ->
                accountService.withdraw("478758", new BigDecimal("10000.00")));
        assertEquals("Saldo no disponible", exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when withdrawing from inactive account")
    void shouldThrowExceptionWhenWithdrawingFromInactiveAccount() {
        // Given
        Account inactiveAccount = new Account("478758", "Ahorro", new BigDecimal("2000.00"), false, 1L);
        inactiveAccount.setId(1L);

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(inactiveAccount));

        // When & Then
        InvalidAccountStateException exception = assertThrows(InvalidAccountStateException.class, () ->
                accountService.withdraw("478758", new BigDecimal("500.00")));
        assertEquals("Cannot withdraw from inactive account", exception.getMessage());
    }

    // ==================== Exists By Account Number Tests ====================

    @Test
    @DisplayName("Should return true when account exists")
    void shouldReturnTrueWhenAccountExists() {
        // Given
        when(accountRepository.existsByAccountNumber("478758")).thenReturn(true);

        // When
        boolean result = accountService.existsByAccountNumber("478758");

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when account does not exist")
    void shouldReturnFalseWhenAccountDoesNotExist() {
        // Given
        when(accountRepository.existsByAccountNumber("999999")).thenReturn(false);

        // When
        boolean result = accountService.existsByAccountNumber("999999");

        // Then
        assertFalse(result);
    }
}
