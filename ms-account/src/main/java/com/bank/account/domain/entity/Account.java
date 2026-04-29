package com.bank.account.domain.entity;

import com.bank.account.domain.exception.InsufficientBalanceException;
import com.bank.account.domain.exception.InvalidAccountStateException;
import com.bank.account.domain.exception.InvalidAccountTypeException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Entity representing a bank account.
 * Part of the domain layer - contains business rules and validations.
 */
public class Account {

    private static final String ACCOUNT_TYPE_SAVINGS = "Ahorro";
    private static final String ACCOUNT_TYPE_CHECKING = "Corriente";

    private Long id;
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private boolean active;
    private Long clientId;

    /**
     * Protected constructor for frameworks and mappers.
     */
    protected Account() {
    }

    /**
     * Constructor for creating a new account.
     *
     * @param accountNumber Unique account number (required)
     * @param accountType   Account type: "Ahorro" or "Corriente" (required)
     * @param initialBalance Initial balance (cannot be negative)
     * @param active        Account status
     * @param clientId      Associated client ID (required)
     */
    public Account(String accountNumber, String accountType, BigDecimal initialBalance,
                   boolean active, Long clientId) {
        this.accountNumber = validateAccountNumber(accountNumber);
        this.accountType = validateAccountType(accountType);
        this.balance = validateBalance(initialBalance);
        this.active = active;
        this.clientId = validateClientId(clientId);
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
        if (!trimmed.matches("^\\d+$")) {
            throw new IllegalArgumentException("Account number must contain only digits");
        }
        return trimmed;
    }

    private String validateAccountType(String accountType) {
        if (accountType == null || accountType.trim().isEmpty()) {
            throw new IllegalArgumentException("Account type is required");
        }
        String normalized = accountType.trim();
        if (!normalized.equals(ACCOUNT_TYPE_SAVINGS) && !normalized.equals(ACCOUNT_TYPE_CHECKING)) {
            throw new InvalidAccountTypeException(
                "Invalid account type: " + normalized + ". Allowed types: " + ACCOUNT_TYPE_SAVINGS + ", " + ACCOUNT_TYPE_CHECKING
            );
        }
        return normalized;
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

    private Long validateClientId(Long clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client ID is required");
        }
        if (clientId <= 0) {
            throw new IllegalArgumentException("Client ID must be positive");
        }
        return clientId;
    }

    // ==================== Business Methods ====================

    /**
     * Deposits money into the account.
     *
     * @param amount Amount to deposit (must be positive)
     * @throws IllegalArgumentException if amount is null, zero or negative
     * @throws InvalidAccountStateException if account is inactive
     */
    public void deposit(BigDecimal amount) {
        if (!this.active) {
            throw new InvalidAccountStateException("Cannot deposit to inactive account");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }

    /**
     * Withdraws money from the account.
     *
     * @param amount Amount to withdraw (must be positive)
     * @throws InsufficientBalanceException if insufficient funds
     * @throws InvalidAccountStateException if account is inactive
     * @throws IllegalArgumentException if amount is null, zero or negative
     */
    public void withdraw(BigDecimal amount) {
        if (!this.active) {
            throw new InvalidAccountStateException("Cannot withdraw from inactive account");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Saldo no disponible");
        }
        this.balance = this.balance.subtract(amount);
    }

    /**
     * Activates the account.
     *
     * @throws InvalidAccountStateException if already active
     */
    public void activate() {
        if (this.active) {
            throw new InvalidAccountStateException("Account is already active");
        }
        this.active = true;
    }

    /**
     * Deactivates the account.
     *
     * @throws InvalidAccountStateException if already inactive
     */
    public void deactivate() {
        if (!this.active) {
            throw new InvalidAccountStateException("Account is already inactive");
        }
        this.active = false;
    }

    // ==================== Getters ====================

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    public Long getClientId() {
        return clientId;
    }

    // ==================== Protected Setters (for mappers/frameworks) ====================

    public void setId(Long id) {
        this.id = id;
    }

    protected void setAccountNumber(String accountNumber) {
        this.accountNumber = validateAccountNumber(accountNumber);
    }

    protected void setAccountType(String accountType) {
        this.accountType = validateAccountType(accountType);
    }

    protected void setBalance(BigDecimal balance) {
        this.balance = validateBalance(balance);
    }

    protected void setActive(boolean active) {
        this.active = active;
    }

    protected void setClientId(Long clientId) {
        this.clientId = validateClientId(clientId);
    }

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return accountNumber != null && accountNumber.equals(account.accountNumber);
    }

    @Override
    public int hashCode() {
        return accountNumber != null ? accountNumber.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountType='" + accountType + '\'' +
                ", balance=" + balance +
                ", active=" + active +
                ", clientId=" + clientId +
                '}';
    }
}
