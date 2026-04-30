package com.bank.account.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for movement responses.
 * Used to return movement data to clients.
 */
@Schema(description = "DTO para la respuesta de datos de un movimiento")
public class MovementResponseDto {

    @Schema(description = "ID interno del movimiento", example = "1")
    private Long id;

    @Schema(description = "Numero de cuenta", example = "478758")
    private String accountNumber;

    @Schema(description = "Fecha y hora del movimiento", example = "2024-04-30T10:00:00")
    private LocalDateTime date;

    @Schema(description = "Tipo de movimiento", example = "Deposito")
    private String type;

    @Schema(description = "Monto del movimiento", example = "100.00")
    private BigDecimal amount;

    @Schema(description = "Saldo resultante despues del movimiento", example = "2100.00")
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
