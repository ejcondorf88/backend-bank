package com.bank.account.domain.entity;

import com.bank.account.domain.exception.InsufficientBalanceException;
import com.bank.account.domain.exception.InvalidAccountStateException;
import com.bank.account.domain.exception.InvalidAccountTypeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Account entity domain logic.
 * Tests business rules, validations, and state transitions.
 */
@DisplayName("Account Entity Tests")
class AccountTest {

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("Should create account with valid data")
    void shouldCreateAccountWithValidData() {
        Account account = new Account("478758", "Ahorro", new BigDecimal("2000.00"), true, 1L);

        assertNotNull(account);
        assertEquals("478758", account.getAccountNumber());
        assertEquals("Ahorro", account.getAccountType());
        assertEquals(0, new BigDecimal("2000.00").compareTo(account.getBalance()));
        assertTrue(account.isActive());
        assertEquals(1L, account.getClientId());
    }

    @Test
    @DisplayName("Should create account with Corriente type")
    void shouldCreateAccountWithCorrienteType() {
        Account account = new Account("225487", "Corriente", new BigDecimal("100.00"), true, 2L);

        assertEquals("Corriente", account.getAccountType());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Ahorro", "Corriente"})
    @DisplayName("Should accept valid account types")
    void shouldAcceptValidAccountTypes(String accountType) {
        Account account = new Account("123456", accountType, BigDecimal.ZERO, true, 1L);

        assertEquals(accountType, account.getAccountType());
    }

    // ==================== Validation Tests ====================

    @Test
    @DisplayName("Should reject null account number")
    void shouldRejectNullAccountNumber() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Account(null, "Ahorro", BigDecimal.ZERO, true, 1L));
        assertEquals("Account number is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject empty account number")
    void shouldRejectEmptyAccountNumber() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Account("   ", "Ahorro", BigDecimal.ZERO, true, 1L));
        assertEquals("Account number is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject account number shorter than 3 characters")
    void shouldRejectShortAccountNumber() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Account("12", "Ahorro", BigDecimal.ZERO, true, 1L));
        assertEquals("Account number must be at least 3 characters", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ABC123", "12.45", "12-34", "12 34"})
    @DisplayName("Should reject account number with non-digit characters")
    void shouldRejectNonDigitAccountNumber(String accountNumber) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Account(accountNumber, "Ahorro", BigDecimal.ZERO, true, 1L));
        assertEquals("Account number must contain only digits", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject null account type")
    void shouldRejectNullAccountType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Account("478758", null, BigDecimal.ZERO, true, 1L));
        assertEquals("Account type is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject invalid account type")
    void shouldRejectInvalidAccountType() {
        InvalidAccountTypeException exception = assertThrows(InvalidAccountTypeException.class, () ->
                new Account("478758", "Invalido", BigDecimal.ZERO, true, 1L));
        assertTrue(exception.getMessage().contains("Invalid account type"));
        assertTrue(exception.getMessage().contains("Ahorro"));
        assertTrue(exception.getMessage().contains("Corriente"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Credito", "Inversion", "AHORRO", "corriente"})
    @DisplayName("Should reject account types that are not exactly Ahorro or Corriente")
    void shouldRejectInvalidAccountTypes(String accountType) {
        assertThrows(InvalidAccountTypeException.class, () ->
                new Account("478758", accountType, BigDecimal.ZERO, true, 1L));
    }

    @Test
    @DisplayName("Should reject null balance")
    void shouldRejectNullBalance() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Account("478758", "Ahorro", null, true, 1L));
        assertEquals("Balance cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject negative balance")
    void shouldRejectNegativeBalance() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Account("478758", "Ahorro", new BigDecimal("-100.00"), true, 1L));
        assertEquals("Balance cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Should accept zero balance")
    void shouldAcceptZeroBalance() {
        Account account = new Account("478758", "Ahorro", BigDecimal.ZERO, true, 1L);

        assertEquals(0, BigDecimal.ZERO.compareTo(account.getBalance()));
    }

    @Test
    @DisplayName("Should reject null client ID")
    void shouldRejectNullClientId() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Account("478758", "Ahorro", BigDecimal.ZERO, true, null));
        assertEquals("Client ID is required", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, -100L})
    @DisplayName("Should reject non-positive client ID")
    void shouldRejectNonPositiveClientId(Long clientId) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Account("478758", "Ahorro", BigDecimal.ZERO, true, clientId));
        assertEquals("Client ID must be positive", exception.getMessage());
    }

    // ==================== Deposit Tests ====================

    @Test
    @DisplayName("Should deposit positive amount")
    void shouldDepositPositiveAmount() {
        Account account = new Account("478758", "Ahorro", new BigDecimal("1000.00"), true, 1L);

        account.deposit(new BigDecimal("500.00"));

        assertEquals(0, new BigDecimal("1500.00").compareTo(account.getBalance()));
    }

    @Test
    @DisplayName("Should reject deposit on inactive account")
    void shouldRejectDepositOnInactiveAccount() {
        Account account = new Account("478758", "Ahorro", new BigDecimal("1000.00"), false, 1L);

        InvalidAccountStateException exception = assertThrows(InvalidAccountStateException.class, () ->
                account.deposit(new BigDecimal("500.00")));
        assertEquals("Cannot deposit to inactive account", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject null deposit amount")
    void shouldRejectNullDepositAmount() {
        Account account = new Account("478758", "Ahorro", new BigDecimal("1000.00"), true, 1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                account.deposit(null));
        assertEquals("Deposit amount must be positive", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"0.00", "-100.00", "-0.01"})
    @DisplayName("Should reject zero or negative deposit amount")
    void shouldRejectZeroOrNegativeDepositAmount(String amount) {
        Account account = new Account("478758", "Ahorro", new BigDecimal("1000.00"), true, 1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                account.deposit(new BigDecimal(amount)));
        assertEquals("Deposit amount must be positive", exception.getMessage());
    }

    // ==================== Withdrawal Tests ====================

    @Test
    @DisplayName("Should withdraw positive amount when sufficient balance")
    void shouldWithdrawPositiveAmountWhenSufficientBalance() {
        Account account = new Account("478758", "Ahorro", new BigDecimal("2000.00"), true, 1L);

        account.withdraw(new BigDecimal("575.00"));

        assertEquals(0, new BigDecimal("1425.00").compareTo(account.getBalance()));
    }

    @Test
    @DisplayName("Should allow withdrawal that leaves zero balance")
    void shouldAllowWithdrawalThatLeavesZeroBalance() {
        Account account = new Account("496825", "Ahorro", new BigDecimal("540.00"), true, 1L);

        account.withdraw(new BigDecimal("540.00"));

        assertEquals(0, BigDecimal.ZERO.compareTo(account.getBalance()));
    }

    @Test
    @DisplayName("Should reject withdrawal on inactive account")
    void shouldRejectWithdrawalOnInactiveAccount() {
        Account account = new Account("478758", "Ahorro", new BigDecimal("2000.00"), false, 1L);

        InvalidAccountStateException exception = assertThrows(InvalidAccountStateException.class, () ->
                account.withdraw(new BigDecimal("500.00")));
        assertEquals("Cannot withdraw from inactive account", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject withdrawal exceeding balance - F3 requirement")
    void shouldRejectWithdrawalExceedingBalance() {
        Account account = new Account("495878", "Ahorro", new BigDecimal("2000.00"), true, 1L);

        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () ->
                account.withdraw(new BigDecimal("2500.00")));
        assertEquals("Saldo no disponible", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject withdrawal from zero balance account - F3 requirement")
    void shouldRejectWithdrawalFromZeroBalanceAccount() {
        Account account = new Account("495878", "Ahorro", BigDecimal.ZERO, true, 1L);

        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () ->
                account.withdraw(new BigDecimal("100.00")));
        assertEquals("Saldo no disponible", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject null withdrawal amount")
    void shouldRejectNullWithdrawalAmount() {
        Account account = new Account("478758", "Ahorro", new BigDecimal("1000.00"), true, 1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                account.withdraw(null));
        assertEquals("Withdrawal amount must be positive", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({"0.00", "-100.00"})
    @DisplayName("Should reject zero or negative withdrawal amount")
    void shouldRejectZeroOrNegativeWithdrawalAmount(String amount) {
        Account account = new Account("478758", "Ahorro", new BigDecimal("1000.00"), true, 1L);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                account.withdraw(new BigDecimal(amount)));
        assertEquals("Withdrawal amount must be positive", exception.getMessage());
    }

    // ==================== State Transition Tests ====================

    @Test
    @DisplayName("Should activate inactive account")
    void shouldActivateInactiveAccount() {
        Account account = new Account("478758", "Ahorro", BigDecimal.ZERO, false, 1L);

        account.activate();

        assertTrue(account.isActive());
    }

    @Test
    @DisplayName("Should reject activation of already active account")
    void shouldRejectActivationOfAlreadyActiveAccount() {
        Account account = new Account("478758", "Ahorro", BigDecimal.ZERO, true, 1L);

        InvalidAccountStateException exception = assertThrows(InvalidAccountStateException.class, () ->
                account.activate());
        assertEquals("Account is already active", exception.getMessage());
    }

    @Test
    @DisplayName("Should deactivate active account")
    void shouldDeactivateActiveAccount() {
        Account account = new Account("478758", "Ahorro", BigDecimal.ZERO, true, 1L);

        account.deactivate();

        assertFalse(account.isActive());
    }

    @Test
    @DisplayName("Should reject deactivation of already inactive account")
    void shouldRejectDeactivationOfAlreadyInactiveAccount() {
        Account account = new Account("478758", "Ahorro", BigDecimal.ZERO, false, 1L);

        InvalidAccountStateException exception = assertThrows(InvalidAccountStateException.class, () ->
                account.deactivate());
        assertEquals("Account is already inactive", exception.getMessage());
    }

    // ==================== Equals and HashCode Tests ====================

    @Test
    @DisplayName("Should be equal when account numbers are equal")
    void shouldBeEqualWhenAccountNumbersAreEqual() {
        Account account1 = new Account("478758", "Ahorro", new BigDecimal("1000.00"), true, 1L);
        Account account2 = new Account("478758", "Corriente", new BigDecimal("500.00"), false, 2L);

        assertEquals(account1, account2);
        assertEquals(account1.hashCode(), account2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when account numbers differ")
    void shouldNotBeEqualWhenAccountNumbersDiffer() {
        Account account1 = new Account("478758", "Ahorro", BigDecimal.ZERO, true, 1L);
        Account account2 = new Account("478759", "Ahorro", BigDecimal.ZERO, true, 1L);

        assertNotEquals(account1, account2);
    }

    @Test
    @DisplayName("Should not be equal to null or different type")
    void shouldNotBeEqualToNullOrDifferentType() {
        Account account = new Account("478758", "Ahorro", BigDecimal.ZERO, true, 1L);

        assertNotEquals(account, null);
        assertNotEquals(account, "478758");
    }

    @Test
    @DisplayName("Should be equal to itself")
    void shouldBeEqualToItself() {
        Account account = new Account("478758", "Ahorro", BigDecimal.ZERO, true, 1L);

        assertEquals(account, account);
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Should trim account number")
    void shouldTrimAccountNumber() {
        Account account = new Account("  478758  ", "Ahorro", BigDecimal.ZERO, true, 1L);

        assertEquals("478758", account.getAccountNumber());
    }

    @Test
    @DisplayName("Should trim account type")
    void shouldTrimAccountType() {
        Account account = new Account("478758", "  Ahorro  ", BigDecimal.ZERO, true, 1L);

        assertEquals("Ahorro", account.getAccountType());
    }

    @Test
    @DisplayName("Should handle BigDecimal precision correctly")
    void shouldHandleBigDecimalPrecisionCorrectly() {
        Account account = new Account("478758", "Ahorro", new BigDecimal("1000.005"), true, 1L);

        // BigDecimal maintains precision
        assertEquals(0, new BigDecimal("1000.005").compareTo(account.getBalance()));
    }

    @Test
    @DisplayName("Should have meaningful toString")
    void shouldHaveMeaningfulToString() {
        Account account = new Account("478758", "Ahorro", new BigDecimal("2000.00"), true, 1L);
        String toString = account.toString();

        assertTrue(toString.contains("478758"));
        assertTrue(toString.contains("Ahorro"));
        assertTrue(toString.contains("2000.00"));
        assertTrue(toString.contains("active=true"));
        assertTrue(toString.contains("clientId=1"));
    }
}
