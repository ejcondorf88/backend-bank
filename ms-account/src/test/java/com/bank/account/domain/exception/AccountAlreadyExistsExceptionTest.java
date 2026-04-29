package com.bank.account.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("AccountAlreadyExistsException Tests")
class AccountAlreadyExistsExceptionTest {

    @Test
    @DisplayName("Should create exception with account number")
    void shouldCreateExceptionWithAccountNumber() {
        AccountAlreadyExistsException exception = new AccountAlreadyExistsException("478758");

        assertEquals("Account already exists with number: 478758", exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }
}
