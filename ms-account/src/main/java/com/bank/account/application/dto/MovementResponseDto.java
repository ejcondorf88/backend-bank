package com.bank.account.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for movement responses.
 * Used to return movement data to clients.
 */
public class MovementResponseDto {

    private Long id;
    private String accountNumber;
    private LocalDateTime date;
    private String type;
    private BigDecimal amount;
    private BigDecimal balance;

    // Default constructor
    public MovementResponseDto() {
    }

    // Constructor with all fields
    public MovementResponseDto(Long id, String accountNumber, LocalDateTime date, String type, BigDecimal amount, BigDecimal balance) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.balance = balance;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "MovementResponseDto{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", date=" + date +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", balance=" + balance +
                '}';
    }
}
