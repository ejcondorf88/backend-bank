package com.bank.account.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for movement creation requests.
 * Uses Bean Validation for input validation.
 */
public class MovementRequestDto {

    @NotBlank(message = "Account number is required")
    @Size(min = 3, max = 20, message = "Account number must be between 3 and 20 characters")
    private String accountNumber;

    @NotBlank(message = "Movement type is required")
    @Pattern(regexp = "^(Deposito|Retiro)$", message = "Movement type must be 'Deposito' or 'Retiro'")
    private String type;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    @Digits(integer = 15, fraction = 2, message = "Amount must have maximum 15 integer digits and 2 decimal places")
    private BigDecimal amount;

    private LocalDateTime date;

    // Default constructor
    public MovementRequestDto() {
    }

    // Constructor with all fields
    public MovementRequestDto(String accountNumber, String type, BigDecimal amount) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
    }

    // Constructor with date
    public MovementRequestDto(String accountNumber, String type, BigDecimal amount, LocalDateTime date) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "MovementRequestDto{" +
                "accountNumber='" + accountNumber + '\'' +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", date=" + date +
                '}';
    }
}
