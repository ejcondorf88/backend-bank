package com.bank.customer.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del dominio - Excepcion InvalidClientStateException
 * Verifica comportamiento de excepciones de dominio
 */
@DisplayName("Pruebas Unitarias - InvalidClientStateException")
class InvalidClientStateExceptionTest {

    @Test
    @DisplayName("Debe crear excepcion con mensaje personalizado")
    void shouldCreateExceptionWithMessage() {
        String message = "Client is already active";
        
        InvalidClientStateException exception = new InvalidClientStateException(message);
        
        assertNotNull(exception);
        assertEquals("Client is already active", exception.getMessage());
    }

    @Test
    @DisplayName("Debe crear excepcion con mensaje diferente")
    void shouldCreateExceptionWithDifferentMessage() {
        String message = "Cannot delete active client";
        
        InvalidClientStateException exception = new InvalidClientStateException(message);
        
        assertEquals("Cannot delete active client", exception.getMessage());
    }

    @Test
    @DisplayName("Debe ser una RuntimeException")
    void shouldBeRuntimeException() {
        InvalidClientStateException exception = new InvalidClientStateException("test");
        
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Debe poder ser lanzada y capturada")
    void shouldBeThrowableAndCatchable() {
        assertThrows(InvalidClientStateException.class, () -> {
            throw new InvalidClientStateException("Invalid state");
        });
    }

    @Test
    @DisplayName("Debe poder obtener el mensaje")
    void shouldGetMessage() {
        InvalidClientStateException exception = new InvalidClientStateException("Test message");
        
        String message = exception.getMessage();
        
        assertNotNull(message);
        assertEquals("Test message", message);
    }

    @Test
    @DisplayName("Debe tener stack trace")
    void shouldHaveStackTrace() {
        InvalidClientStateException exception = new InvalidClientStateException("test");
        
        StackTraceElement[] stackTrace = exception.getStackTrace();
        
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
    }

    @Test
    @DisplayName("Debe permitir mensaje vacio")
    void shouldAllowEmptyMessage() {
        InvalidClientStateException exception = new InvalidClientStateException("");
        
        assertEquals("", exception.getMessage());
    }

    @Test
    @DisplayName("Debe permitir mensaje null")
    void shouldAllowNullMessage() {
        InvalidClientStateException exception = new InvalidClientStateException(null);
        
        assertNull(exception.getMessage());
    }
}
