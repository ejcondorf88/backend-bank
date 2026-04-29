package com.bank.account.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Movement entity domain logic.
 * Tests business rules, validations, and factory methods.
 * Implements F5 requirement: Unit tests for domain layer.
 */
@DisplayName("Movement Entity Tests")
class MovementTest {

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("Should create movement with valid data")
    void shouldCreateMovementWithValidData() {
        LocalDateTime now = LocalDateTime.now();
        Movement movement = new Movement("478758", now, "Deposito",
                new BigDecimal("600.00"), new BigDecimal("2600.00"));

        assertNotNull(movement);
        assertEquals("478758", movement.getAccountNumber());
        assertEquals(now, movement.getDate());
        assertEquals("Deposito", movement.getType());
        assertEquals(0, new BigDecimal("600.00").compareTo(movement.getAmount()));
        assertEquals(0, new BigDecimal("2600.00").compareTo(movement.getBalance()));
    }

    @Test
    @DisplayName("Should create deposit with factory method")
    void shouldCreateDepositWithFactoryMethod() {
        Movement movement = Movement.createDeposit("478758",
                new BigDecimal("600.00"), new BigDecimal("2600.00"));

        assertNotNull(movement);
        assertEquals("478758", movement.getAccountNumber());
        assertEquals("Deposito", movement.getType());
        assertTrue(movement.isDeposit());
        assertFalse(movement.isWithdrawal());
        // Amount should be positive for deposit
        assertTrue(movement.getAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Should create withdrawal with factory method")
    void shouldCreateWithdrawalWithFactoryMethod() {
        Movement movement = Movement.createWithdrawal("478758",
                new BigDecimal("575.00"), new BigDecimal("1425.00"));

        assertNotNull(movement);
        assertEquals("478758", movement.getAccountNumber());
        assertEquals("Retiro", movement.getType());
        assertTrue(movement.isWithdrawal());
        assertFalse(movement.isDeposit());
        // Amount should be negative for withdrawal
        assertTrue(movement.getAmount().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    @DisplayName("Should auto-assign current date when null")
    void shouldAutoAssignCurrentDateWhenNull() {
        Movement movement = new Movement("478758", null, "Deposito",
                new BigDecimal("600.00"), new BigDecimal("2600.00"));

        assertNotNull(movement.getDate());
        // Should be very recent (within last second)
        assertTrue(movement.getDate().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Deposito", "Retiro"})
    @DisplayName("Should accept valid movement types")
    void shouldAcceptValidMovementTypes(String movementType) {
        Movement movement = new Movement("478758", LocalDateTime.now(), movementType,
                new BigDecimal("100.00"), new BigDecimal("1000.00"));

        assertEquals(movementType, movement.getType());
    }

    // ==================== Validation Tests ====================

    @Test
    @DisplayName("Should reject null account number")
    void shouldRejectNullAccountNumber() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Movement(null, LocalDateTime.now(), "Deposito",
                        new BigDecimal("100.00"), new BigDecimal("1000.00")));
        assertEquals("Account number is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject empty account number")
    void shouldRejectEmptyAccountNumber() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Movement(" ", LocalDateTime.now(), "Deposito",
                        new BigDecimal("100.00"), new BigDecimal("1000.00")));
        assertEquals("Account number is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject account number shorter than 3 characters")
    void shouldRejectShortAccountNumber() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Movement("12", LocalDateTime.now(), "Deposito",
                        new BigDecimal("100.00"), new BigDecimal("1000.00")));
        assertEquals("Account number must be at least 3 characters", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject null movement type")
    void shouldRejectNullMovementType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Movement("478758", LocalDateTime.now(), null,
                        new BigDecimal("100.00"), new BigDecimal("1000.00")));
        assertEquals("Movement type is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject invalid movement type")
    void shouldRejectInvalidMovementType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Movement("478758", LocalDateTime.now(), "Invalido",
                        new BigDecimal("100.00"), new BigDecimal("1000.00")));
        assertTrue(exception.getMessage().contains("Invalid movement type"));
        assertTrue(exception.getMessage().contains("Deposito"));
        assertTrue(exception.getMessage().contains("Retiro"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"Transferencia", "Pago", "Cobro", "", "  "})
    @DisplayName("Should reject various invalid movement types")
    void shouldRejectVariousInvalidMovementTypes(String invalidType) {
        assertThrows(IllegalArgumentException.class, () ->
                new Movement("478758", LocalDateTime.now(), invalidType,
                        new BigDecimal("100.00"), new BigDecimal("1000.00")));
    }

    @Test
    @DisplayName("Should reject null amount")
    void shouldRejectNullAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Movement("478758", LocalDateTime.now(), "Deposito",
                        null, new BigDecimal("1000.00")));
        assertEquals("Amount cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject zero amount")
    void shouldRejectZeroAmount() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Movement("478758", LocalDateTime.now(), "Deposito",
                        BigDecimal.ZERO, new BigDecimal("1000.00")));
        assertEquals("Amount cannot be zero", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject null balance")
    void shouldRejectNullBalance() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Movement("478758", LocalDateTime.now(), "Deposito",
                        new BigDecimal("100.00"), null));
        assertEquals("Balance cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject negative balance")
    void shouldRejectNegativeBalance() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                new Movement("478758", LocalDateTime.now(), "Deposito",
                        new BigDecimal("100.00"), new BigDecimal("-100.00")));
        assertEquals("Balance cannot be negative", exception.getMessage());
    }

    // ==================== Business Method Tests ====================

    @Test
    @DisplayName("Should identify deposit correctly")
    void shouldIdentifyDepositCorrectly() {
        Movement deposit = Movement.createDeposit("478758",
                new BigDecimal("600.00"), new BigDecimal("2600.00"));

        assertTrue(deposit.isDeposit());
        assertFalse(deposit.isWithdrawal());
    }

    @Test
    @DisplayName("Should identify withdrawal correctly")
    void shouldIdentifyWithdrawalCorrectly() {
        Movement withdrawal = Movement.createWithdrawal("478758",
                new BigDecimal("575.00"), new BigDecimal("1425.00"));

        assertTrue(withdrawal.isWithdrawal());
        assertFalse(withdrawal.isDeposit());
    }

    @Test
    @DisplayName("Should return absolute amount for deposit")
    void shouldReturnAbsoluteAmountForDeposit() {
        Movement deposit = Movement.createDeposit("478758",
                new BigDecimal("600.00"), new BigDecimal("2600.00"));

        assertEquals(0, new BigDecimal("600.00").compareTo(deposit.getAbsoluteAmount()));
        assertTrue(deposit.getAbsoluteAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Should return absolute amount for withdrawal")
    void shouldReturnAbsoluteAmountForWithdrawal() {
        Movement withdrawal = Movement.createWithdrawal("478758",
                new BigDecimal("575.00"), new BigDecimal("1425.00"));

        // Amount stored is negative
        assertTrue(withdrawal.getAmount().compareTo(BigDecimal.ZERO) < 0);
        // But absolute amount is positive
        assertEquals(0, new BigDecimal("575.00").compareTo(withdrawal.getAbsoluteAmount()));
        assertTrue(withdrawal.getAbsoluteAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @ParameterizedTest
    @CsvSource({
            "100.00, 100.00",
            "575.50, 575.50",
            "0.01, 0.01",
            "999999.99, 999999.99"
    })
    @DisplayName("Should return correct absolute amount for various values")
    void shouldReturnCorrectAbsoluteAmount(String inputAmount, String expectedAbsolute) {
        Movement withdrawal = Movement.createWithdrawal("478758",
                new BigDecimal(inputAmount), new BigDecimal("1000.00"));

        assertEquals(0, new BigDecimal(expectedAbsolute).compareTo(withdrawal.getAbsoluteAmount()));
    }

    // ==================== Edge Case Tests ====================

    @Test
    @DisplayName("Should handle large amounts")
    void shouldHandleLargeAmounts() {
        BigDecimal largeAmount = new BigDecimal("999999999999.99");
        BigDecimal largeBalance = new BigDecimal("9999999999999.99");

        Movement movement = Movement.createDeposit("478758", largeAmount, largeBalance);

        assertEquals(0, largeAmount.compareTo(movement.getAmount()));
        assertEquals(0, largeBalance.compareTo(movement.getBalance()));
    }

    @Test
    @DisplayName("Should handle very small amounts")
    void shouldHandleVerySmallAmounts() {
        BigDecimal smallAmount = new BigDecimal("0.01");

        Movement movement = Movement.createDeposit("478758", smallAmount, new BigDecimal("100.01"));

        assertEquals(0, smallAmount.compareTo(movement.getAmount()));
    }

    @Test
    @DisplayName("Should trim whitespace from account number")
    void shouldTrimWhitespaceFromAccountNumber() {
        Movement movement = new Movement("  478758  ", LocalDateTime.now(), "Deposito",
                new BigDecimal("100.00"), new BigDecimal("1000.00"));

        assertEquals("478758", movement.getAccountNumber());
    }

    @Test
    @DisplayName("Should trim whitespace from movement type")
    void shouldTrimWhitespaceFromMovementType() {
        Movement movement = new Movement("478758", LocalDateTime.now(), "  Deposito  ",
                new BigDecimal("100.00"), new BigDecimal("1000.00"));

        assertEquals("Deposito", movement.getType());
    }

    // ==================== Protected Constructor Tests ====================

    @Test
    @DisplayName("Should allow protected constructor for frameworks")
    void shouldAllowProtectedConstructorForFrameworks() {
        // This simulates what JPA/MapStruct would do
        Movement movement = new Movement() {
            // Anonymous subclass to access protected constructor
        };

        assertNotNull(movement);
        // All fields should be null/zero/false
        assertNull(movement.getAccountNumber());
        assertNull(movement.getDate());
        assertNull(movement.getType());
        assertNull(movement.getAmount());
        assertNull(movement.getBalance());
    }

    // ==================== Equals and HashCode Tests ====================

    @Test
    @DisplayName("Should be equal when same ID")
    void shouldBeEqualWhenSameId() {
        Movement movement1 = Movement.createDeposit("478758",
                new BigDecimal("600.00"), new BigDecimal("2600.00"));
        movement1.setId(1L);

        Movement movement2 = Movement.createWithdrawal("225487",
                new BigDecimal("100.00"), new BigDecimal("900.00"));
        movement2.setId(1L);

        assertEquals(movement1, movement2);
        assertEquals(movement1.hashCode(), movement2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when different IDs")
    void shouldNotBeEqualWhenDifferentIds() {
        Movement movement1 = Movement.createDeposit("478758",
                new BigDecimal("600.00"), new BigDecimal("2600.00"));
        movement1.setId(1L);

        Movement movement2 = Movement.createDeposit("478758",
                new BigDecimal("600.00"), new BigDecimal("2600.00"));
        movement2.setId(2L);

        assertNotEquals(movement1, movement2);
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        Movement movement = Movement.createDeposit("478758",
                new BigDecimal("600.00"), new BigDecimal("2600.00"));
        movement.setId(1L);

        assertNotEquals(movement, null);
    }

    @Test
    @DisplayName("Should not be equal to different class")
    void shouldNotBeEqualToDifferentClass() {
        Movement movement = Movement.createDeposit("478758",
                new BigDecimal("600.00"), new BigDecimal("2600.00"));
        movement.setId(1L);

        assertNotEquals(movement, "not a movement");
    }

    @Test
    @DisplayName("Should be equal to itself")
    void shouldBeEqualToItself() {
        Movement movement = Movement.createDeposit("478758",
                new BigDecimal("600.00"), new BigDecimal("2600.00"));
        movement.setId(1L);

        assertEquals(movement, movement);
    }

    // ==================== ToString Tests ====================

    @Test
    @DisplayName("Should have meaningful toString")
    void shouldHaveMeaningfulToString() {
        Movement movement = Movement.createDeposit("478758",
                new BigDecimal("600.00"), new BigDecimal("2600.00"));
        movement.setId(1L);

        String toString = movement.toString();

        assertNotNull(toString);
        assertTrue(toString.contains("Movement"));
        assertTrue(toString.contains("478758"));
        assertTrue(toString.contains("Deposito"));
    }

    // ==================== F2 Specific Scenarios ====================

    @Test
    @DisplayName("F2: Should store deposit with positive amount")
    void f2_shouldStoreDepositWithPositiveAmount() {
        // Scenario from F2: Deposito actualiza saldo
        Movement movement = Movement.createDeposit("225487",
                new BigDecimal("600.00"), new BigDecimal("700.00"));

        assertEquals("Deposito", movement.getType());
        // El movimiento debe tener valor positivo
        assertTrue(movement.getAmount().compareTo(BigDecimal.ZERO) > 0);
        assertEquals(0, new BigDecimal("600.00").compareTo(movement.getAmount()));
        // El saldo debe ser 700
        assertEquals(0, new BigDecimal("700.00").compareTo(movement.getBalance()));
    }

    @Test
    @DisplayName("F2: Should store withdrawal with negative amount")
    void f2_shouldStoreWithdrawalWithNegativeAmount() {
        // Scenario from F2: Retiro actualiza saldo
        Movement movement = Movement.createWithdrawal("478758",
                new BigDecimal("575.00"), new BigDecimal("1425.00"));

        assertEquals("Retiro", movement.getType());
        // El movimiento debe tener valor negativo
        assertTrue(movement.getAmount().compareTo(BigDecimal.ZERO) < 0);
        // El valor absoluto es 575
        assertEquals(0, new BigDecimal("575.00").compareTo(movement.getAbsoluteAmount()));
        // El saldo debe ser 1425
        assertEquals(0, new BigDecimal("1425.00").compareTo(movement.getBalance()));
    }

    @Test
    @DisplayName("F2: Should handle withdrawal leaving zero balance")
    void f2_shouldHandleWithdrawalLeavingZeroBalance() {
        // Scenario from F2: Retiro deja saldo en cero
        Movement movement = Movement.createWithdrawal("496825",
                new BigDecimal("540.00"), BigDecimal.ZERO);

        assertEquals("Retiro", movement.getType());
        assertEquals(0, new BigDecimal("540.00").compareTo(movement.getAbsoluteAmount()));
        // Saldo disponible debe ser 0
        assertEquals(0, BigDecimal.ZERO.compareTo(movement.getBalance()));
    }

    @Test
    @DisplayName("F2: Movement should have automatic date")
    void f2_movementShouldHaveAutomaticDate() {
        // Scenario from F2: Movimiento debe tener fecha automatica
        Movement movement = Movement.createDeposit("225487",
                new BigDecimal("600.00"), new BigDecimal("700.00"));

        assertNotNull(movement.getDate());
        // La fecha debe ser reciente (creada automaticamente)
        assertTrue(movement.getDate().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(movement.getDate().isAfter(LocalDateTime.now().minusSeconds(5)));
    }

    @Test
    @DisplayName("F2: Movement date should be ISO 8601 format compatible")
    void f2_movementDateShouldBeIso8601Compatible() {
        Movement movement = Movement.createDeposit("225487",
                new BigDecimal("600.00"), new BigDecimal("700.00"));

        LocalDateTime date = movement.getDate();

        // Verificar que la fecha tiene los componentes esperados
        assertTrue(date.getYear() >= 2026);
        assertNotNull(date.toString()); // Debe poder convertirse a string (ISO 8601)
    }
}
