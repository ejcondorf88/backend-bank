package com.bank.account.application.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO for account creation and update requests.
 * Uses Bean Validation for input validation.
 */
public class AccountRequestDto {

    @NotBlank(message = "Account number is required")
    @Size(min = 3, max = 20, message = "Account number must be between 3 and 20 characters")
    @Pattern(regexp = "^\\d+$", message = "Account number must contain only digits")
    private String accountNumber;

    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "^(Ahorro|Corriente)$", message = "Account type must be 'Ahorro' or 'Corriente'")
    private String accountType;

    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
    @Digits(integer = 15, fraction = 2, message = "Balance must have maximum 15 integer digits and 2 decimal places")
    private BigDecimal initialBalance;

    @NotNull(message = "Active status is required")
    private Boolean active;

    @NotNull(message = "Client ID is required")
    @Positive(message = "Client ID must be positive")
    private Long clientId;

    // Default constructor
    public AccountRequestDto() {
    }

    // Constructor with all fields
    public AccountRequestDto(String accountNumber, String accountType, BigDecimal initialBalance, Boolean active, Long clientId) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.initialBalance = initialBalance;
        this.active = active;
        this.clientId = clientId;
    }

    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "AccountRequestDto{" +
                "accountNumber='" + accountNumber + '\'' +
                ", accountType='" + accountType + '\'' +
                ", initialBalance=" + initialBalance +
                ", active=" + active +
                ", clientId=" + clientId +
                '}';
    }
}
