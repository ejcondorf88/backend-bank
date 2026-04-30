package com.bank.account.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for each line of the Account Statement report (F4).
 * Completely in English.
 */
@Schema(description = "DTO representing a detailed line of the account statement report")
public class StatementLineDto {

    @Schema(description = "Transaction date", example = "2022-02-10T00:00:00")
    private LocalDateTime date;

    @Schema(description = "Full name of the client", example = "Marianela Montalvo")
    private String clientName;

    @Schema(description = "Account number", example = "225487")
    private String accountNumber;

    @Schema(description = "Account type", example = "Savings")
    private String accountType;

    @Schema(description = "Initial balance before the movement", example = "100.00")
    private BigDecimal initialBalance;

    @Schema(description = "Current account status", example = "true")
    private Boolean active;

    @Schema(description = "Movement amount", example = "600.00")
    private BigDecimal movementAmount;

    @Schema(description = "Available balance after the movement", example = "700.00")
    private BigDecimal availableBalance;

    // ==================== Constructors ====================

    public StatementLineDto() {
    }

    public StatementLineDto(LocalDateTime date, String clientName, String accountNumber,
                            String accountType, BigDecimal initialBalance, Boolean active,
                            BigDecimal movementAmount, BigDecimal availableBalance) {
        this.date = date;
        this.clientName = clientName;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.initialBalance = initialBalance;
        this.active = active;
        this.movementAmount = movementAmount;
        this.availableBalance = availableBalance;
    }

    // ==================== Getters & Setters ====================

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public BigDecimal getInitialBalance() { return initialBalance; }
    public void setInitialBalance(BigDecimal initialBalance) { this.initialBalance = initialBalance; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public BigDecimal getMovementAmount() { return movementAmount; }
    public void setMovementAmount(BigDecimal movementAmount) { this.movementAmount = movementAmount; }

    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }
}
