package com.bank.customer.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del dominio - Excepcion ClientNotFoundException
 * Verifica comportamiento de excepciones de dominio
 */
@DisplayName("Pruebas Unitarias - ClientNotFoundException")
class ClientNotFoundExceptionTest {

    @Test
    @DisplayName("Debe crear excepcion con ID de cliente")
    void shouldCreateExceptionWithClientId() {
        Long clientId = 1L;
        
        ClientNotFoundException exception = new ClientNotFoundException(clientId);
        
        assertNotNull(exception);
        assertEquals("Client not found with id: 1", exception.getMessage());
    }

    @Test
    @DisplayName("Debe crear excepcion con identificacion nula")
    void shouldCreateExceptionWithNullIdentification() {
        String nullIdentification = null;
        ClientNotFoundException exception = new ClientNotFoundException(nullIdentification);
        
        assertNotNull(exception);
        assertEquals("Client not found with identification: null", exception.getMessage());
    }

    @Test
    @DisplayName("Debe crear excepcion con ID diferente")
    void shouldCreateExceptionWithDifferentId() {
        Long clientId = 999L;
        
        ClientNotFoundException exception = new ClientNotFoundException(clientId);
        
        assertEquals("Client not found with id: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Debe ser una RuntimeException")
    void shouldBeRuntimeException() {
        ClientNotFoundException exception = new ClientNotFoundException(1L);
        
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Debe poder ser lanzada y capturada")
    void shouldBeThrowableAndCatchable() {
        assertThrows(ClientNotFoundException.class, () -> {
            throw new ClientNotFoundException(1L);
        });
    }

    @Test
    @DisplayName("Debe poder obtener el mensaje")
    void shouldGetMessage() {
        ClientNotFoundException exception = new ClientNotFoundException(42L);
        
        String message = exception.getMessage();
        
        assertNotNull(message);
        assertTrue(message.contains("42"));
    }

    @Test
    @DisplayName("Debe tener stack trace")
    void shouldHaveStackTrace() {
        ClientNotFoundException exception = new ClientNotFoundException(1L);
        
        StackTraceElement[] stackTrace = exception.getStackTrace();
        
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
    }

    @Test
    @DisplayName("Debe poder usar el ID en el mensaje")
    void shouldIncludeIdInMessage() {
        Long testId = 12345L;
        
        ClientNotFoundException exception = new ClientNotFoundException(testId);
        
        assertTrue(exception.getMessage().contains(testId.toString()));
    }
}
