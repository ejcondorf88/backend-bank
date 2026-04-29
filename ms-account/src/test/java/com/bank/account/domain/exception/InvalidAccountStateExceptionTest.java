package com.bank.account.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InvalidAccountStateException Tests")
class InvalidAccountStateExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateExceptionWithMessage() {
        InvalidAccountStateException exception = new InvalidAccountStateException("Account is already active");

        assertEquals("Account is already active", exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should create exception with message and cause")
    void shouldCreateExceptionWithMessageAndCause() {
        Throwable cause = new IllegalArgumentException("Original error");
        InvalidAccountStateException exception = new InvalidAccountStateException("Account is already active", cause);

        assertEquals("Account is already active", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
