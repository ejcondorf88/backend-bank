package com.bank.account.application.dto;

import java.math.BigDecimal;

/**
 * DTO for account responses.
 * Used to return account data to clients.
 */
public class AccountResponseDto {

    private Long id;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private boolean active;
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
