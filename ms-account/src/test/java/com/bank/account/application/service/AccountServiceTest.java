package com.bank.account.application.service;

import com.bank.account.application.dto.AccountRequestDto;
import com.bank.account.application.dto.AccountResponseDto;
import com.bank.account.application.dto.TransactionRequestDto;
import com.bank.account.application.mapper.AccountApplicationMapper;
import com.bank.account.domain.entity.Account;
import com.bank.account.domain.exception.AccountAlreadyExistsException;
import com.bank.account.domain.exception.AccountNotFoundException;
import com.bank.account.domain.exception.InsufficientBalanceException;
import com.bank.account.domain.exception.InvalidAccountStateException;
import com.bank.account.domain.port.out.AccountRepository;
import com.bank.account.domain.port.out.MovementRepository;
import com.bank.account.application.mapper.MovementApplicationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
 * Unit tests for AccountApplicationService.
 * Tests application layer use cases with mocked repository and mapper.
 * No Spring context - pure Mockito tests.
 * Replaces old AccountServiceImplTest (the service was a duplicate).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Account Service Tests")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountApplicationMapper accountMapper;

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private MovementApplicationMapper movementMapper;

    private AccountApplicationService accountService;

    private Account account;
    private AccountRequestDto requestDto;
    private AccountResponseDto responseDto;

    @BeforeEach
    void setUp() {
        accountService = new AccountApplicationService(
                accountRepository, 
                accountMapper, 
                movementRepository, 
                movementMapper
        );

        account = new Account("478758", "Ahorro", new BigDecimal("2000.00"), true, 1L);
        account.setId(1L);

        requestDto = new AccountRequestDto("478758", "Ahorro", new BigDecimal("2000.00"), true, 1L);

        responseDto = new AccountResponseDto(1L, "478758", "Ahorro", new BigDecimal("2000.00"), true, 1L);
    }

    // ==================== Create Account Tests ====================

    @Test
    @DisplayName("Should create account successfully")
    void shouldCreateAccountSuccessfully() {
        when(accountRepository.existsByAccountNumber("478758")).thenReturn(false);
        when(accountMapper.toDomain(requestDto)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toResponseDto(account)).thenReturn(responseDto);

        AccountResponseDto result = accountService.createAccount(requestDto);

        assertNotNull(result);
        assertEquals("478758", result.getAccountNumber());
        assertEquals("Ahorro", result.getAccountType());
        verify(accountRepository).existsByAccountNumber("478758");
        verify(accountMapper).toDomain(requestDto);
        verify(accountRepository).save(account);
        verify(accountMapper).toResponseDto(account);
    }

    @Test
    @DisplayName("Should throw exception when creating account with duplicate number")
    void shouldThrowExceptionWhenCreatingAccountWithDuplicateNumber() {
        when(accountRepository.existsByAccountNumber("478758")).thenReturn(true);

        assertThrows(AccountAlreadyExistsException.class,
                () -> accountService.createAccount(requestDto));
        verify(accountRepository, never()).save(any());
    }

    // ==================== Find Account Tests ====================

    @Test
    @DisplayName("Should find account by account number")
    void shouldFindAccountByAccountNumber() {
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));
        when(accountMapper.toResponseDto(account)).thenReturn(responseDto);

        AccountResponseDto result = accountService.findByAccountNumber("478758");

        assertNotNull(result);
        assertEquals("478758", result.getAccountNumber());
    }

    @Test
    @DisplayName("Should throw exception when account not found by number")
    void shouldThrowExceptionWhenAccountNotFoundByNumber() {
        when(accountRepository.findByAccountNumber("999999")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> accountService.findByAccountNumber("999999"));
    }

    @Test
    @DisplayName("Should find account by id")
    void shouldFindAccountById() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountMapper.toResponseDto(account)).thenReturn(responseDto);

        AccountResponseDto result = accountService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Should throw exception when account not found by id")
    void shouldThrowExceptionWhenAccountNotFoundById() {
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> accountService.findById(999L));
    }

    // ==================== Find All Accounts Tests ====================

    @Test
    @DisplayName("Should return all accounts")
    void shouldReturnAllAccounts() {
        Account account2 = new Account("225487", "Corriente", new BigDecimal("1000.00"), true, 2L);
        account2.setId(2L);
        AccountResponseDto responseDto2 = new AccountResponseDto(2L, "225487", "Corriente",
                new BigDecimal("1000.00"), true, 2L);

        when(accountRepository.findAll()).thenReturn(Arrays.asList(account, account2));
        when(accountMapper.toResponseDto(account)).thenReturn(responseDto);
        when(accountMapper.toResponseDto(account2)).thenReturn(responseDto2);

        List<AccountResponseDto> result = accountService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should return empty list when no accounts")
    void shouldReturnEmptyListWhenNoAccounts() {
        when(accountRepository.findAll()).thenReturn(Collections.emptyList());

        List<AccountResponseDto> result = accountService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== Find By Client ID Tests ====================

    @Test
    @DisplayName("Should return accounts by client id")
    void shouldReturnAccountsByClientId() {
        when(accountRepository.findByClientId(1L)).thenReturn(Arrays.asList(account));
        when(accountMapper.toResponseDto(account)).thenReturn(responseDto);

        List<AccountResponseDto> result = accountService.findByClientId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ==================== Delete Account Tests ====================

    @Test
    @DisplayName("Should delete account successfully")
    void shouldDeleteAccountSuccessfully() {
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));

        accountService.deleteAccount("478758");

        verify(accountRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing account")
    void shouldThrowExceptionWhenDeletingNonExistingAccount() {
        when(accountRepository.findByAccountNumber("999999")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> accountService.deleteAccount("999999"));
        verify(accountRepository, never()).deleteById(any());
    }

    // ==================== Activate/Deactivate Tests ====================

    @Test
    @DisplayName("Should activate account successfully")
    void shouldActivateAccountSuccessfully() {
        Account inactiveAccount = new Account("478758", "Ahorro", new BigDecimal("2000.00"), false, 1L);
        inactiveAccount.setId(1L);
        AccountResponseDto activatedDto = new AccountResponseDto(1L, "478758", "Ahorro",
                new BigDecimal("2000.00"), true, 1L);

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(inactiveAccount));
        when(accountRepository.save(inactiveAccount)).thenReturn(inactiveAccount);
        when(accountMapper.toResponseDto(inactiveAccount)).thenReturn(activatedDto);

        AccountResponseDto result = accountService.activateAccount("478758");

        assertNotNull(result);
        assertTrue(result.isActive());
        verify(accountRepository).save(inactiveAccount);
    }

    @Test
    @DisplayName("Should deactivate account successfully")
    void shouldDeactivateAccountSuccessfully() {
        AccountResponseDto deactivatedDto = new AccountResponseDto(1L, "478758", "Ahorro",
                new BigDecimal("2000.00"), false, 1L);

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toResponseDto(account)).thenReturn(deactivatedDto);

        AccountResponseDto result = accountService.deactivateAccount("478758");

        assertNotNull(result);
        assertFalse(result.isActive());
        verify(accountRepository).save(account);
    }

    // ==================== Deposit Tests ====================

    @Test
    @DisplayName("Should deposit successfully")
    void shouldDepositSuccessfully() {
        TransactionRequestDto txnRequest = new TransactionRequestDto("478758", new BigDecimal("500.00"));
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toResponseDto(account)).thenReturn(responseDto);
        when(movementMapper.fromTransaction(anyString(), anyString(), any(), any())).thenReturn(mock(com.bank.account.domain.entity.Movement.class));

        AccountResponseDto result = accountService.deposit(txnRequest);

        assertNotNull(result);
        assertEquals(0, new BigDecimal("2500.00").compareTo(account.getBalance()));
        verify(accountRepository).save(account);
        verify(movementRepository).save(any());
    }

    @Test
    @DisplayName("Should throw exception when depositing to non-existing account")
    void shouldThrowExceptionWhenDepositingToNonExistingAccount() {
        TransactionRequestDto txnRequest = new TransactionRequestDto("999999", new BigDecimal("500.00"));
        when(accountRepository.findByAccountNumber("999999")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> accountService.deposit(txnRequest));
    }

    @Test
    @DisplayName("Should throw exception when depositing to inactive account")
    void shouldThrowExceptionWhenDepositingToInactiveAccount() {
        Account inactiveAccount = new Account("478758", "Ahorro", new BigDecimal("2000.00"), false, 1L);
        inactiveAccount.setId(1L);
        TransactionRequestDto txnRequest = new TransactionRequestDto("478758", new BigDecimal("500.00"));

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(inactiveAccount));

        assertThrows(InvalidAccountStateException.class,
                () -> accountService.deposit(txnRequest));
    }

    // ==================== Withdraw Tests ====================

    @Test
    @DisplayName("Should withdraw successfully")
    void shouldWithdrawSuccessfully() {
        TransactionRequestDto txnRequest = new TransactionRequestDto("478758", new BigDecimal("500.00"));
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toResponseDto(account)).thenReturn(responseDto);
        when(movementMapper.fromTransaction(anyString(), anyString(), any(), any())).thenReturn(mock(com.bank.account.domain.entity.Movement.class));

        AccountResponseDto result = accountService.withdraw(txnRequest);

        assertNotNull(result);
        assertEquals(0, new BigDecimal("1500.00").compareTo(account.getBalance()));
        verify(accountRepository).save(account);
        verify(movementRepository).save(any());
    }

    @Test
    @DisplayName("Should throw exception when withdrawing from non-existing account")
    void shouldThrowExceptionWhenWithdrawingFromNonExistingAccount() {
        TransactionRequestDto txnRequest = new TransactionRequestDto("999999", new BigDecimal("500.00"));
        when(accountRepository.findByAccountNumber("999999")).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> accountService.withdraw(txnRequest));
    }

    @Test
    @DisplayName("Should throw exception when withdrawing with insufficient balance - F3")
    void shouldThrowExceptionWhenWithdrawingWithInsufficientBalance() {
        TransactionRequestDto txnRequest = new TransactionRequestDto("478758", new BigDecimal("10000.00"));
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(account));

        assertThrows(InsufficientBalanceException.class,
                () -> accountService.withdraw(txnRequest));
    }

    @Test
    @DisplayName("Should throw exception when withdrawing from inactive account")
    void shouldThrowExceptionWhenWithdrawingFromInactiveAccount() {
        Account inactiveAccount = new Account("478758", "Ahorro", new BigDecimal("2000.00"), false, 1L);
        inactiveAccount.setId(1L);
        TransactionRequestDto txnRequest = new TransactionRequestDto("478758", new BigDecimal("500.00"));

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(inactiveAccount));

        assertThrows(InvalidAccountStateException.class,
                () -> accountService.withdraw(txnRequest));
    }
}