package com.bank.account.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for report responses (F4 requirement).
 * Contains account state with movement history.
 */
public class ReportResponseDto {

    private Long clientId;
    private String clientName;
    private String accountNumber;
    private String accountType;
    private BigDecimal currentBalance;
    private boolean active;
    private LocalDateTime reportDate;
    private List<MovementDetailDto> movements;
    private int totalMovements;
    private BigDecimal totalDeposits;
    private BigDecimal totalWithdrawals;

    // Default constructor
    public ReportResponseDto() {
    }

    // Constructor with account info
    public ReportResponseDto(String accountNumber, String accountType, BigDecimal currentBalance, boolean active) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.currentBalance = currentBalance;
        this.active = active;
    }

    // Getters and Setters
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
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

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }

    public List<MovementDetailDto> getMovements() {
        return movements;
    }

    public void setMovements(List<MovementDetailDto> movements) {
        this.movements = movements;
    }

    public int getTotalMovements() {
        return totalMovements;
    }

    public void setTotalMovements(int totalMovements) {
        this.totalMovements = totalMovements;
    }

    public BigDecimal getTotalDeposits() {
        return totalDeposits;
    }

    public void setTotalDeposits(BigDecimal totalDeposits) {
        this.totalDeposits = totalDeposits;
    }

    public BigDecimal getTotalWithdrawals() {
        return totalWithdrawals;
    }

    public void setTotalWithdrawals(BigDecimal totalWithdrawals) {
        this.totalWithdrawals = totalWithdrawals;
    }

    @Override
    public String toString() {
        return "ReportResponseDto{" +
                "clientId=" + clientId +
                ", clientName='" + clientName + '\'' +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountType='" + accountType + '\'' +
                ", currentBalance=" + currentBalance +
                ", active=" + active +
                ", reportDate=" + reportDate +
                ", totalMovements=" + totalMovements +
                ", totalDeposits=" + totalDeposits +
                ", totalWithdrawals=" + totalWithdrawals +
                '}';
    }

    /**
     * Inner DTO for movement details in report.
     */
    public static class MovementDetailDto {
        private LocalDateTime date;
        private String type;
        private BigDecimal amount;
        private BigDecimal balance;

        public MovementDetailDto() {
        }

        public MovementDetailDto(LocalDateTime date, String type, BigDecimal amount, BigDecimal balance) {
            this.date = date;
            this.type = type;
            this.amount = amount;
            this.balance = balance;
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
    }
}
