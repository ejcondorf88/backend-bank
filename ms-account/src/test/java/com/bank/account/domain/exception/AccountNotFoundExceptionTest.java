package com.bank.account.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AccountNotFoundException Tests")
class AccountNotFoundExceptionTest {

    @Test
    @DisplayName("Should create exception with account number")
    void shouldCreateExceptionWithAccountNumber() {
        AccountNotFoundException exception = new AccountNotFoundException("478758");

        assertEquals("Account not found with number: 478758", exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Should create exception with ID")
    void shouldCreateExceptionWithId() {
        AccountNotFoundException exception = new AccountNotFoundException(123L);

        assertEquals("Account not found with id: 123", exception.getMessage());
    }
}
