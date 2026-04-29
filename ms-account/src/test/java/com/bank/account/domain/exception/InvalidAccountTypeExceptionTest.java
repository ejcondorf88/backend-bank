package com.bank.account.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InvalidAccountTypeException Tests")
class InvalidAccountTypeExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateExceptionWithMessage() {
        InvalidAccountTypeException exception = new InvalidAccountTypeException("Invalid type");

        assertEquals("Invalid type", exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should create exception with message and cause")
    void shouldCreateExceptionWithMessageAndCause() {
        Throwable cause = new IllegalArgumentException("Original error");
        InvalidAccountTypeException exception = new InvalidAccountTypeException("Invalid type", cause);

        assertEquals("Invalid type", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
