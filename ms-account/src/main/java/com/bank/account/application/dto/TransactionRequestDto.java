package com.bank.account.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO for deposit and withdrawal requests.
 */
@Schema(description = "DTO para solicitudes de transacciones (deposito/retiro)")
public class TransactionRequestDto {

    @Schema(description = "Numero de cuenta para la transaccion", example = "478758")
    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @Schema(description = "Monto de la transaccion (debe ser positivo)", example = "100.00")
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 15, fraction = 2, message = "Amount must have maximum 15 integer digits and 2 decimal places")
    private BigDecimal amount;

    // Default constructor
    public TransactionRequestDto() {
    }

    // Constructor with all fields
    public TransactionRequestDto(String accountNumber, BigDecimal amount) {
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TransactionRequestDto{" +
                "accountNumber='" + accountNumber + '\'' +
                ", amount=" + amount +
                '}';
    }
}
