package com.bank.account.application.service;

import com.bank.account.application.dto.MovementRequestDto;
import com.bank.account.application.dto.MovementResponseDto;
import com.bank.account.application.mapper.MovementApplicationMapper;
import com.bank.account.domain.entity.Account;
import com.bank.account.domain.entity.Movement;
import com.bank.account.domain.exception.AccountNotFoundException;
import com.bank.account.domain.exception.InsufficientBalanceException;
import com.bank.account.domain.exception.InvalidAccountStateException;
import com.bank.account.domain.exception.MovementNotFoundException;
import com.bank.account.domain.repository.AccountRepository;
import com.bank.account.domain.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MovementApplicationService.
 * Tests application layer use cases with mocked repositories.
 * No Spring context - pure Mockito tests.
 * Implements F5 requirement: Unit tests for application layer.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MovementApplicationService Tests")
class MovementApplicationServiceTest {

    @Mock
    private MovementRepository movementRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private MovementApplicationMapper movementMapper;

    @InjectMocks
    private MovementApplicationService movementService;

    private Account activeAccount;
    private Movement depositMovement;
    private Movement withdrawalMovement;
    private MovementResponseDto depositResponseDto;
    private MovementResponseDto withdrawalResponseDto;

    @BeforeEach
    void setUp() {
        // Create active account with balance
        activeAccount = new Account("478758", "Ahorro", new BigDecimal("2000.00"), true, 1L);
        activeAccount.setId(1L);

        // Create movements
        depositMovement = Movement.createDeposit("478758",
                new BigDecimal("600.00"), new BigDecimal("2600.00"));
        depositMovement.setId(1L);

        withdrawalMovement = Movement.createWithdrawal("478758",
                new BigDecimal("575.00"), new BigDecimal("1425.00"));
        withdrawalMovement.setId(2L);

        // Create response DTOs
        depositResponseDto = new MovementResponseDto(1L, "478758",
                depositMovement.getDate(), "Deposito",
                new BigDecimal("600.00"), new BigDecimal("2600.00"));

        withdrawalResponseDto = new MovementResponseDto(2L, "478758",
                withdrawalMovement.getDate(), "Retiro",
                new BigDecimal("-575.00"), new BigDecimal("1425.00"));
    }

    // ==================== Create Deposit Tests ====================

    @Test
    @DisplayName("F2: Should create deposit successfully")
    void shouldCreateDepositSuccessfully() {
        // Given
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(activeAccount));
        when(accountRepository.save(activeAccount)).thenReturn(activeAccount);
        when(movementRepository.save(any(Movement.class))).thenReturn(depositMovement);
        when(movementMapper.toResponseDto(depositMovement)).thenReturn(depositResponseDto);

        // When
        MovementResponseDto result = movementService.createDeposit("478758", new BigDecimal("600.00"));

        // Then
        assertNotNull(result);
        assertEquals("478758", result.getAccountNumber());
        assertEquals("Deposito", result.getType());
        assertEquals(0, new BigDecimal("600.00").compareTo(result.getAmount()));
        verify(accountRepository).findByAccountNumber("478758");
        verify(accountRepository).save(activeAccount);
        verify(movementRepository).save(any(Movement.class));
    }

    @Test
    @DisplayName("F2: Should throw exception when depositing to non-existing account")
    void shouldThrowExceptionWhenDepositingToNonExistingAccount() {
        // Given
        when(accountRepository.findByAccountNumber("999999")).thenReturn(Optional.empty());

        // When & Then
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () ->
                movementService.createDeposit("999999", new BigDecimal("600.00")));
        assertEquals("Account not found with number: 999999", exception.getMessage());
        verify(movementRepository, never()).save(any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("F2: Should throw exception when depositing to inactive account")
    void shouldThrowExceptionWhenDepositingToInactiveAccount() {
        // Given
        Account inactiveAccount = new Account("478758", "Ahorro", new BigDecimal("2000.00"), false, 1L);
        inactiveAccount.setId(1L);
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(inactiveAccount));

        // When & Then
        InvalidAccountStateException exception = assertThrows(InvalidAccountStateException.class, () ->
                movementService.createDeposit("478758", new BigDecimal("600.00")));
        assertEquals("Cannot deposit to inactive account", exception.getMessage());
        verify(movementRepository, never()).save(any());
    }

    // ==================== Create Withdrawal Tests ====================

    @Test
    @DisplayName("F2: Should create withdrawal successfully")
    void shouldCreateWithdrawalSuccessfully() {
        // Given
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(activeAccount));
        when(accountRepository.save(activeAccount)).thenReturn(activeAccount);
        when(movementRepository.save(any(Movement.class))).thenReturn(withdrawalMovement);
        when(movementMapper.toResponseDto(withdrawalMovement)).thenReturn(withdrawalResponseDto);

        // When
        MovementResponseDto result = movementService.createWithdrawal("478758", new BigDecimal("575.00"));

        // Then
        assertNotNull(result);
        assertEquals("478758", result.getAccountNumber());
        assertEquals("Retiro", result.getType());
        assertEquals(0, new BigDecimal("-575.00").compareTo(result.getAmount()));
        verify(accountRepository).findByAccountNumber("478758");
        verify(accountRepository).save(activeAccount);
        verify(movementRepository).save(any(Movement.class));
    }

    @Test
    @DisplayName("F3: Should throw exception when withdrawing with insufficient balance")
    void shouldThrowExceptionWhenWithdrawingWithInsufficientBalance() {
        // Given
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(activeAccount));

        // When & Then
        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () ->
                movementService.createWithdrawal("478758", new BigDecimal("10000.00")));
        assertEquals("Saldo no disponible", exception.getMessage());
        verify(movementRepository, never()).save(any());
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("F2: Should throw exception when withdrawing from non-existing account")
    void shouldThrowExceptionWhenWithdrawingFromNonExistingAccount() {
        // Given
        when(accountRepository.findByAccountNumber("999999")).thenReturn(Optional.empty());

        // When & Then
        AccountNotFoundException exception = assertThrows(AccountNotFoundException.class, () ->
                movementService.createWithdrawal("999999", new BigDecimal("500.00")));
        assertEquals("Account not found with number: 999999", exception.getMessage());
    }

    @Test
    @DisplayName("F2: Should throw exception when withdrawing from inactive account")
    void shouldThrowExceptionWhenWithdrawingFromInactiveAccount() {
        // Given
        Account inactiveAccount = new Account("478758", "Ahorro", new BigDecimal("2000.00"), false, 1L);
        inactiveAccount.setId(1L);
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(inactiveAccount));

        // When & Then
        InvalidAccountStateException exception = assertThrows(InvalidAccountStateException.class, () ->
                movementService.createWithdrawal("478758", new BigDecimal("500.00")));
        assertEquals("Cannot withdraw from inactive account", exception.getMessage());
    }

    // ==================== Create Movement from DTO Tests ====================

    @Test
    @DisplayName("F2: Should create movement from DTO - deposit")
    void shouldCreateMovementFromDtoDeposit() {
        // Given
        MovementRequestDto requestDto = new MovementRequestDto("478758", "Deposito", new BigDecimal("600.00"));

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(activeAccount));
        when(accountRepository.save(activeAccount)).thenReturn(activeAccount);
        when(movementMapper.fromTransaction(any(), any(), any(), any())).thenReturn(depositMovement);
        when(movementRepository.save(depositMovement)).thenReturn(depositMovement);
        when(movementMapper.toResponseDto(depositMovement)).thenReturn(depositResponseDto);

        // When
        MovementResponseDto result = movementService.createMovement(requestDto);

        // Then
        assertNotNull(result);
        assertEquals("Deposito", result.getType());
        verify(movementMapper).fromTransaction("478758", "Deposito", new BigDecimal("600.00"), new BigDecimal("2600.00"));
        verify(movementRepository).save(depositMovement);
    }

    @Test
    @DisplayName("F2: Should create movement from DTO - withdrawal")
    void shouldCreateMovementFromDtoWithdrawal() {
        // Given
        MovementRequestDto requestDto = new MovementRequestDto("478758", "Retiro", new BigDecimal("575.00"));

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(activeAccount));
        when(accountRepository.save(activeAccount)).thenReturn(activeAccount);
        when(movementMapper.fromTransaction(any(), any(), any(), any())).thenReturn(withdrawalMovement);
        when(movementRepository.save(withdrawalMovement)).thenReturn(withdrawalMovement);
        when(movementMapper.toResponseDto(withdrawalMovement)).thenReturn(withdrawalResponseDto);

        // When
        MovementResponseDto result = movementService.createMovement(requestDto);

        // Then
        assertNotNull(result);
        assertEquals("Retiro", result.getType());
        verify(movementMapper).fromTransaction("478758", "Retiro", new BigDecimal("575.00"), new BigDecimal("1425.00"));
    }

    @Test
    @DisplayName("F2: Should throw exception when creating movement on inactive account")
    void shouldThrowExceptionWhenCreatingMovementOnInactiveAccount() {
        // Given
        MovementRequestDto requestDto = new MovementRequestDto("478758", "Deposito", new BigDecimal("600.00"));
        Account inactiveAccount = new Account("478758", "Ahorro", new BigDecimal("2000.00"), false, 1L);
        inactiveAccount.setId(1L);

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(inactiveAccount));

        // When & Then
        InvalidAccountStateException exception = assertThrows(InvalidAccountStateException.class, () ->
                movementService.createMovement(requestDto));
        assertEquals("Cannot create movement on inactive account", exception.getMessage());
    }

    @Test
    @DisplayName("F2: Should throw exception when creating movement with invalid type")
    void shouldThrowExceptionWhenCreatingMovementWithInvalidType() {
        // Given
        MovementRequestDto requestDto = new MovementRequestDto("478758", "InvalidType", new BigDecimal("600.00"));

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(activeAccount));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                movementService.createMovement(requestDto));
        assertTrue(exception.getMessage().contains("Invalid movement type"));
    }

    // ==================== Find Movement Tests ====================

    @Test
    @DisplayName("F2: Should find movement by id")
    void shouldFindMovementById() {
        // Given
        when(movementRepository.findById(1L)).thenReturn(Optional.of(depositMovement));
        when(movementMapper.toResponseDto(depositMovement)).thenReturn(depositResponseDto);

        // When
        MovementResponseDto result = movementService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Deposito", result.getType());
    }

    @Test
    @DisplayName("F2: Should throw exception when movement not found by id")
    void shouldThrowExceptionWhenMovementNotFoundById() {
        // Given
        when(movementRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        MovementNotFoundException exception = assertThrows(MovementNotFoundException.class, () ->
                movementService.findById(999L));
        assertEquals("Movement not found with id: 999", exception.getMessage());
    }

    // ==================== Find All Movements Tests ====================

    @Test
    @DisplayName("F2: Should return all movements")
    void shouldReturnAllMovements() {
        // Given
        List<Movement> movements = Arrays.asList(depositMovement, withdrawalMovement);
        when(movementRepository.findAll()).thenReturn(movements);
        when(movementMapper.toResponseDto(depositMovement)).thenReturn(depositResponseDto);
        when(movementMapper.toResponseDto(withdrawalMovement)).thenReturn(withdrawalResponseDto);

        // When
        List<MovementResponseDto> result = movementService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("F2: Should return empty list when no movements")
    void shouldReturnEmptyListWhenNoMovements() {
        // Given
        when(movementRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<MovementResponseDto> result = movementService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== Find By Account Number Tests ====================

    @Test
    @DisplayName("F2: Should return movements by account number")
    void shouldReturnMovementsByAccountNumber() {
        // Given
        List<Movement> movements = Arrays.asList(depositMovement, withdrawalMovement);
        when(movementRepository.findByAccountNumber("478758")).thenReturn(movements);
        when(movementMapper.toResponseDto(depositMovement)).thenReturn(depositResponseDto);
        when(movementMapper.toResponseDto(withdrawalMovement)).thenReturn(withdrawalResponseDto);

        // When
        List<MovementResponseDto> result = movementService.findByAccountNumber("478758");

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(movementRepository).findByAccountNumber("478758");
    }

    @Test
    @DisplayName("F2: Should return empty list when account has no movements")
    void shouldReturnEmptyListWhenAccountHasNoMovements() {
        // Given
        when(movementRepository.findByAccountNumber("999999")).thenReturn(Collections.emptyList());

        // When
        List<MovementResponseDto> result = movementService.findByAccountNumber("999999");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== Find By Client ID Tests (F4) ====================

    @Test
    @DisplayName("F4: Should return movements by client id")
    void shouldReturnMovementsByClientId() {
        // Given
        List<Movement> movements = Arrays.asList(depositMovement, withdrawalMovement);
        when(movementRepository.findByClientId(1L)).thenReturn(movements);
        when(movementMapper.toResponseDto(depositMovement)).thenReturn(depositResponseDto);
        when(movementMapper.toResponseDto(withdrawalMovement)).thenReturn(withdrawalResponseDto);

        // When
        List<MovementResponseDto> result = movementService.findByClientId(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(movementRepository).findByClientId(1L);
    }

    @Test
    @DisplayName("F4: Should return empty list when client has no movements")
    void shouldReturnEmptyListWhenClientHasNoMovements() {
        // Given
        when(movementRepository.findByClientId(999L)).thenReturn(Collections.emptyList());

        // When
        List<MovementResponseDto> result = movementService.findByClientId(999L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== Find By Date Range Tests (F4) ====================

    @Test
    @DisplayName("F4: Should return movements by date range")
    void shouldReturnMovementsByDateRange() {
        // Given
        String fechaInicio = "2022-02-01";
        String fechaFin = "2022-02-15";

        List<Movement> movements = Arrays.asList(depositMovement);
        when(movementRepository.findByDateBetween(any(), any())).thenReturn(movements);
        when(movementMapper.toResponseDto(depositMovement)).thenReturn(depositResponseDto);

        // When
        List<MovementResponseDto> result = movementService.findByDateRange(fechaInicio, fechaFin);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(movementRepository).findByDateBetween(any(), any());
    }

    @Test
    @DisplayName("F4: Should return movements by account and date range")
    void shouldReturnMovementsByAccountAndDateRange() {
        // Given
        String accountNumber = "478758";
        String fechaInicio = "2022-02-01";
        String fechaFin = "2022-02-15";

        List<Movement> movements = Arrays.asList(depositMovement, withdrawalMovement);
        when(movementRepository.findByAccountNumberAndDateBetween(eq(accountNumber), any(), any())).thenReturn(movements);
        when(movementMapper.toResponseDto(depositMovement)).thenReturn(depositResponseDto);
        when(movementMapper.toResponseDto(withdrawalMovement)).thenReturn(withdrawalResponseDto);

        // When
        List<MovementResponseDto> result = movementService.findByAccountAndDateRange(accountNumber, fechaInicio, fechaFin);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(movementRepository).findByAccountNumberAndDateBetween(eq(accountNumber), any(), any());
    }

    // ==================== Delete Movement Tests ====================

    @Test
    @DisplayName("F2: Should delete movement successfully")
    void shouldDeleteMovementSuccessfully() {
        // Given
        when(movementRepository.findById(1L)).thenReturn(Optional.of(depositMovement));

        // When
        movementService.deleteMovement(1L);

        // Then
        verify(movementRepository).deleteById(1L);
    }

    @Test
    @DisplayName("F2: Should throw exception when deleting non-existing movement")
    void shouldThrowExceptionWhenDeletingNonExistingMovement() {
        // Given
        when(movementRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        MovementNotFoundException exception = assertThrows(MovementNotFoundException.class, () ->
                movementService.deleteMovement(999L));
        assertEquals("Movement not found with id: 999", exception.getMessage());
        verify(movementRepository, never()).deleteById(any());
    }

    // ==================== Get Movement Count Tests ====================

    @Test
    @DisplayName("F2: Should return movement count for account")
    void shouldReturnMovementCountForAccount() {
        // Given
        when(movementRepository.countByAccountNumber("478758")).thenReturn(5L);

        // When
        long result = movementService.getMovementCount("478758");

        // Then
        assertEquals(5L, result);
        verify(movementRepository).countByAccountNumber("478758");
    }

    @Test
    @DisplayName("F2: Should return zero when account has no movements")
    void shouldReturnZeroWhenAccountHasNoMovements() {
        // Given
        when(movementRepository.countByAccountNumber("999999")).thenReturn(0L);

        // When
        long result = movementService.getMovementCount("999999");

        // Then
        assertEquals(0L, result);
    }

    // ==================== Edge Case Tests ====================

    @Test
    @DisplayName("F2: Should handle deposit that leaves zero balance")
    void shouldHandleDepositThatLeavesZeroBalance() {
        // Given - Account starts with 0 balance
        Account zeroBalanceAccount = new Account("225487", "Ahorro", BigDecimal.ZERO, true, 1L);
        zeroBalanceAccount.setId(2L);

        Movement deposit = Movement.createDeposit("225487", new BigDecimal("100.00"), new BigDecimal("100.00"));
        deposit.setId(3L);

        MovementResponseDto depositDto = new MovementResponseDto(3L, "225487",
                deposit.getDate(), "Deposito", new BigDecimal("100.00"), new BigDecimal("100.00"));

        when(accountRepository.findByAccountNumber("225487")).thenReturn(Optional.of(zeroBalanceAccount));
        when(accountRepository.save(zeroBalanceAccount)).thenReturn(zeroBalanceAccount);
        when(movementRepository.save(any(Movement.class))).thenReturn(deposit);
        when(movementMapper.toResponseDto(deposit)).thenReturn(depositDto);

        // When
        MovementResponseDto result = movementService.createDeposit("225487", new BigDecimal("100.00"));

        // Then
        assertNotNull(result);
        assertEquals(0, new BigDecimal("100.00").compareTo(result.getBalance()));
    }

    @Test
    @DisplayName("F2: Should handle withdrawal that leaves zero balance")
    void shouldHandleWithdrawalThatLeavesZeroBalance() {
        // Given - Account with exact balance
        Account exactBalanceAccount = new Account("496825", "Ahorro", new BigDecimal("540.00"), true, 1L);
        exactBalanceAccount.setId(3L);

        Movement withdrawal = Movement.createWithdrawal("496825", new BigDecimal("540.00"), BigDecimal.ZERO);
        withdrawal.setId(4L);

        MovementResponseDto withdrawalDto = new MovementResponseDto(4L, "496825",
                withdrawal.getDate(), "Retiro", new BigDecimal("-540.00"), BigDecimal.ZERO);

        when(accountRepository.findByAccountNumber("496825")).thenReturn(Optional.of(exactBalanceAccount));
        when(accountRepository.save(exactBalanceAccount)).thenReturn(exactBalanceAccount);
        when(movementRepository.save(any(Movement.class))).thenReturn(withdrawal);
        when(movementMapper.toResponseDto(withdrawal)).thenReturn(withdrawalDto);

        // When
        MovementResponseDto result = movementService.createWithdrawal("496825", new BigDecimal("540.00"));

        // Then
        assertNotNull(result);
        assertEquals(0, BigDecimal.ZERO.compareTo(result.getBalance()));
    }

    @Test
    @DisplayName("F3: Should throw exception for withdrawal exceeding balance")
    void shouldThrowExceptionForWithdrawalExceedingBalance() {
        // Given
        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(activeAccount));

        // When & Then - Try to withdraw more than balance
        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () ->
                movementService.createWithdrawal("478758", new BigDecimal("2500.00")));
        assertEquals("Saldo no disponible", exception.getMessage());
        verify(movementRepository, never()).save(any());
    }

    @Test
    @DisplayName("F2: Should handle large deposit amounts")
    void shouldHandleLargeDepositAmounts() {
        // Given
        BigDecimal largeAmount = new BigDecimal("999999.99");
        Movement largeMovement = Movement.createDeposit("478758", largeAmount, new BigDecimal("1001999.99"));
        largeMovement.setId(5L);

        MovementResponseDto largeDto = new MovementResponseDto(5L, "478758",
                largeMovement.getDate(), "Deposito", largeAmount, new BigDecimal("1001999.99"));

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(activeAccount));
        when(accountRepository.save(activeAccount)).thenReturn(activeAccount);
        when(movementRepository.save(any(Movement.class))).thenReturn(largeMovement);
        when(movementMapper.toResponseDto(largeMovement)).thenReturn(largeDto);

        // When
        MovementResponseDto result = movementService.createDeposit("478758", largeAmount);

        // Then
        assertNotNull(result);
        assertEquals(0, largeAmount.compareTo(result.getAmount()));
    }

    @Test
    @DisplayName("F2: Should handle very small deposit amounts")
    void shouldHandleVerySmallDepositAmounts() {
        // Given
        BigDecimal smallAmount = new BigDecimal("0.01");
        Movement smallMovement = Movement.createDeposit("478758", smallAmount, new BigDecimal("2000.01"));
        smallMovement.setId(6L);

        MovementResponseDto smallDto = new MovementResponseDto(6L, "478758",
                smallMovement.getDate(), "Deposito", smallAmount, new BigDecimal("2000.01"));

        when(accountRepository.findByAccountNumber("478758")).thenReturn(Optional.of(activeAccount));
        when(accountRepository.save(activeAccount)).thenReturn(activeAccount);
        when(movementRepository.save(any(Movement.class))).thenReturn(smallMovement);
        when(movementMapper.toResponseDto(smallMovement)).thenReturn(smallDto);

        // When
        MovementResponseDto result = movementService.createDeposit("478758", smallAmount);

        // Then
        assertNotNull(result);
        assertEquals(0, smallAmount.compareTo(result.getAmount()));
    }
}
