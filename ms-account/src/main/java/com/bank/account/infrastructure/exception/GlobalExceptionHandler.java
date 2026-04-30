package com.bank.account.infrastructure.exception;

import com.bank.account.domain.exception.AccountAlreadyExistsException;
import com.bank.account.domain.exception.AccountNotFoundException;
import com.bank.account.domain.exception.InsufficientBalanceException;
import com.bank.account.domain.exception.InvalidAccountStateException;
import com.bank.account.domain.exception.InvalidAccountTypeException;
import com.bank.account.domain.exception.InvalidMovementTypeException;
import com.bank.account.domain.exception.ClientNotFoundException;
import com.bank.account.domain.exception.MovementNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler global de excepciones para el microservicio de cuentas.
 * Mapea excepciones de dominio a códigos HTTP apropiados.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones cuando una cuenta no es encontrada.
     * HTTP 404 - Not Found
     */
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleAccountNotFoundException(AccountNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    /**
     * Maneja excepciones cuando un cliente no es encontrado en la proyección local.
     * HTTP 404 - Not Found
     */
    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleClientNotFoundException(ClientNotFoundException ex) {
        return buildError(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    /**
     * Maneja excepciones cuando una cuenta ya existe.
     * HTTP 409 - Conflict
     */
    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleAccountAlreadyExistsException(AccountAlreadyExistsException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.CONFLICT.value());
        error.put("error", "Conflict");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Maneja excepciones cuando no hay saldo suficiente.
     * HTTP 400 - Bad Request
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Maneja excepciones cuando el estado de la cuenta es inválido.
     * HTTP 400 - Bad Request
     */
    @ExceptionHandler(InvalidAccountStateException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidAccountStateException(InvalidAccountStateException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Maneja excepciones cuando el tipo de cuenta es inválido.
     * HTTP 400 - Bad Request
     */
    @ExceptionHandler(InvalidAccountTypeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidAccountTypeException(InvalidAccountTypeException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Maneja excepciones cuando un movimiento no es encontrado.
     * HTTP 404 - Not Found
     */
    @ExceptionHandler(MovementNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMovementNotFoundException(MovementNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Maneja excepciones cuando el tipo de movimiento es inválido.
     * HTTP 400 - Bad Request
     */
    @ExceptionHandler(InvalidMovementTypeException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidMovementTypeException(InvalidMovementTypeException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Maneja errores de validación de @Valid en los DTOs de request.
     * HTTP 400 - Bad Request con detalle por campo.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Validation Failed");
        error.put("fields", fieldErrors);
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Maneja excepciones de argumentos inválidos (validaciones de dominio).
     * HTTP 400 - Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");
        error.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Maneja excepciones de estado inválido (reglas de negocio).
     * HTTP 409 - Conflict
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalStateException(IllegalStateException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.CONFLICT.value());
        error.put("error", "Conflict");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    // =========================================================================
    // Handlers de infraestructura/HTTP — evitan 500 en errores del cliente
    // =========================================================================

    /**
     * Maneja JSON malformado en el cuerpo del request.
     * HTTP 400 - Bad Request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Bad Request",
                "Malformed JSON request body. Check the syntax and try again.");
    }

    /**
     * Maneja métodos HTTP no soportados para un endpoint.
     * HTTP 405 - Method Not Allowed
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        return buildError(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed", ex.getMessage());
    }

    /**
     * Maneja parámetros de request faltantes (query params).
     * HTTP 400 - Bad Request
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        return buildError(HttpStatus.BAD_REQUEST, "Bad Request",
                "Required parameter '" + ex.getParameterName() + "' is missing");
    }

    /**
     * Maneja violaciones de integridad de base de datos.
     * HTTP 409 - Conflict
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        return buildError(HttpStatus.CONFLICT, "Conflict",
                "Data integrity violation. The operation conflicts with existing data.");
    }

    // =========================================================================
    // Handler genérico
    // =========================================================================

    /**
     * Maneja excepciones genéricas.
     * HTTP 500 - Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred");
    }

    // =========================================================================
    // Helper
    // =========================================================================

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
