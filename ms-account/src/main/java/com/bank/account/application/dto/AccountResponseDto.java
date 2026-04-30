package com.bank.account.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

/**
 * DTO for account responses.
 * Used to return account data to clients.
 */
@Schema(description = "DTO para la respuesta de datos de una cuenta")
public class AccountResponseDto {

    @Schema(description = "ID interno de la cuenta", example = "1")
    private Long id;

    @Schema(description = "Numero de cuenta", example = "478758")
    private String accountNumber;

    @Schema(description = "Tipo de cuenta", example = "Ahorro")
    private String accountType;

    @Schema(description = "Saldo actual de la cuenta", example = "2000.00")
    private BigDecimal balance;

    @Schema(description = "Estado de la cuenta", example = "true")
    private boolean active;

    @Schema(description = "ID del cliente propietario", example = "1")
    private Long clientId;

    // Default constructor
    public AccountResponseDto() {
    }

    // Constructor with all fields
    public AccountResponseDto(Long id, String accountNumber, String accountType, BigDecimal balance, boolean active, Long clientId) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.active = active;
        this.clientId = clientId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
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
        return "AccountResponseDto{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                ", active=" + active +
                ", clientId=" + clientId +
                '}';
    }
}
