package com.bank.account.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a bank account movement/transaction.
 * Part of the domain layer - contains business rules and validations.
 * Implements F2 requirement: "registrar movimientos" with historical record.
 */
public class Movement {

    private static final String TYPE_DEPOSIT = "Deposito";
    private static final String TYPE_WITHDRAWAL = "Retiro";

    private Long id;
    private String accountNumber;
    private LocalDateTime date;
    private String type;
    private BigDecimal amount;
    private BigDecimal balance;

    /**
     * Protected constructor for frameworks and mappers.
     */
    protected Movement() {
    }

    /**
     * Constructor for creating a new movement.
     *
     * @param accountNumber Account number associated with this movement (required)
     * @param date Date and time of the movement (auto-assigned if null)
     * @param type Movement type: "Deposito" or "Retiro" (required)
     * @param amount Amount of the movement - positive for deposit, negative for withdrawal (required)
     * @param balance Account balance after this movement (required)
     */
    public Movement(String accountNumber, LocalDateTime date, String type,
                    BigDecimal amount, BigDecimal balance) {
        this.accountNumber = validateAccountNumber(accountNumber);
        this.date = date != null ? date : LocalDateTime.now();
        this.type = validateType(type);
        this.amount = validateAmount(amount);
        this.balance = validateBalance(balance);
    }

    /**
     * Constructor for creating a deposit movement.
     * Amount will be stored as positive.
     *
     * @param accountNumber Account number
     * @param amount Deposit amount (must be positive)
     * @param balance Balance after deposit
     */
    public static Movement createDeposit(String accountNumber, BigDecimal amount, BigDecimal balance) {
        return new Movement(accountNumber, LocalDateTime.now(), TYPE_DEPOSIT, amount.abs(), balance);
    }

    /**
     * Constructor for creating a withdrawal movement.
     * Amount will be stored as negative.
     *
     * @param accountNumber Account number
     * @param amount Withdrawal amount (must be positive, will be stored as negative)
     * @param balance Balance after withdrawal
     */
    public static Movement createWithdrawal(String accountNumber, BigDecimal amount, BigDecimal balance) {
        return new Movement(accountNumber, LocalDateTime.now(), TYPE_WITHDRAWAL,
                amount.abs().negate(), balance);
    }

    // ==================== Validations ====================

    private String validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Account number is required");
        }
        String trimmed = accountNumber.trim();
        if (trimmed.length() < 3) {
            throw new IllegalArgumentException("Account number must be at least 3 characters");
        }
        return trimmed;
    }

    private String validateType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Movement type is required");
        }
        String normalized = type.trim();
        if (!normalized.equals(TYPE_DEPOSIT) && !normalized.equals(TYPE_WITHDRAWAL)) {
            throw new IllegalArgumentException(
                    "Invalid movement type: " + normalized + ". Allowed types: " + TYPE_DEPOSIT + ", " + TYPE_WITHDRAWAL
            );
        }
        return normalized;
    }

    private BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Amount cannot be zero");
        }
        return amount;
    }

    private BigDecimal validateBalance(BigDecimal balance) {
        if (balance == null) {
            throw new IllegalArgumentException("Balance cannot be null");
        }
        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        return balance;
    }

    // ==================== Business Methods ====================

    /**
     * Checks if this movement is a deposit.
     *
     * @return true if deposit, false otherwise
     */
    public boolean isDeposit() {
        return TYPE_DEPOSIT.equals(this.type);
    }

    /**
     * Checks if this movement is a withdrawal.
     *
     * @return true if withdrawal, false otherwise
     */
    public boolean isWithdrawal() {
        return TYPE_WITHDRAWAL.equals(this.type);
    }

    /**
     * Gets the absolute amount (always positive).
     *
     * @return absolute amount
     */
    public BigDecimal getAbsoluteAmount() {
        return amount.abs();
    }

    // ==================== Getters ====================

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    // ==================== Protected Setters (for mappers/frameworks) ====================

    public void setId(Long id) {
        this.id = id;
    }

    protected void setAccountNumber(String accountNumber) {
        this.accountNumber = validateAccountNumber(accountNumber);
    }

    protected void setDate(LocalDateTime date) {
        this.date = date != null ? date : LocalDateTime.now();
    }

    protected void setType(String type) {
        this.type = validateType(type);
    }

    protected void setAmount(BigDecimal amount) {
        this.amount = validateAmount(amount);
    }

    protected void setBalance(BigDecimal balance) {
        this.balance = validateBalance(balance);
    }

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movement movement = (Movement) o;
        return id != null && id.equals(movement.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Movement{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", date=" + date +
                ", type='" + type + '\'' +
                ", amount=" + amount +
                ", balance=" + balance +
                '}';
    }
}
