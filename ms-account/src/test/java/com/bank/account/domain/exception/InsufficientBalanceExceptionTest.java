package com.bank.account.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InsufficientBalanceException Tests")
class InsufficientBalanceExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateExceptionWithMessage() {
        InsufficientBalanceException exception = new InsufficientBalanceException("Saldo no disponible");

        assertEquals("Saldo no disponible", exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should create exception with message and cause")
    void shouldCreateExceptionWithMessageAndCause() {
        Throwable cause = new IllegalArgumentException("Original error");
        InsufficientBalanceException exception = new InsufficientBalanceException("Saldo no disponible", cause);

        assertEquals("Saldo no disponible", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
