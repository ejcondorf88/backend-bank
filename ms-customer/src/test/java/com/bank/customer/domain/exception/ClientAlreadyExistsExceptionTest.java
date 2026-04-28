package com.bank.customer.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias del dominio - Excepcion ClientAlreadyExistsException
 * Verifica comportamiento de excepciones de dominio
 */
@DisplayName("Pruebas Unitarias - ClientAlreadyExistsException")
class ClientAlreadyExistsExceptionTest {

    @Test
    @DisplayName("Debe crear excepcion con identificacion")
    void shouldCreateExceptionWithIdentification() {
        String identification = "1720456325";
        
        ClientAlreadyExistsException exception = new ClientAlreadyExistsException(identification);
        
        assertNotNull(exception);
        assertEquals("Client already exists with identification: 1720456325", exception.getMessage());
    }

    @Test
    @DisplayName("Debe crear excepcion con identificacion diferente")
    void shouldCreateExceptionWithDifferentIdentification() {
        String identification = "0926548751";
        
        ClientAlreadyExistsException exception = new ClientAlreadyExistsException(identification);
        
        assertEquals("Client already exists with identification: 0926548751", exception.getMessage());
    }

    @Test
    @DisplayName("Debe ser una RuntimeException")
    void shouldBeRuntimeException() {
        ClientAlreadyExistsException exception = new ClientAlreadyExistsException("123");
        
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Debe poder ser lanzada y capturada")
    void shouldBeThrowableAndCatchable() {
        assertThrows(ClientAlreadyExistsException.class, () -> {
            throw new ClientAlreadyExistsException("1720456325");
        });
    }

    @Test
    @DisplayName("Debe poder obtener el mensaje")
    void shouldGetMessage() {
        ClientAlreadyExistsException exception = new ClientAlreadyExistsException("1720456325");
        
        String message = exception.getMessage();
        
        assertNotNull(message);
        assertTrue(message.contains("1720456325"));
    }

    @Test
    @DisplayName("Debe tener stack trace")
    void shouldHaveStackTrace() {
        ClientAlreadyExistsException exception = new ClientAlreadyExistsException("1720456325");
        
        StackTraceElement[] stackTrace = exception.getStackTrace();
        
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
    }
}
