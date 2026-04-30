package com.bank.account.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for report requests (F4 requirement).
 * Uses Bean Validation for input validation.
 */
public class ReportRequestDto {

    private Long clientId;
    private String accountNumber;

    @NotNull(message = "Start date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Start date must be in format yyyy-MM-dd")
    private String startDate;

    @NotNull(message = "End date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "End date must be in format yyyy-MM-dd")
    private String endDate;

    // Default constructor
    public ReportRequestDto() {
    }

    // Constructor with all fields
    public ReportRequestDto(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Constructor with client
    public ReportRequestDto(Long clientId, String startDate, String endDate) {
        this.clientId = clientId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Constructor with account
    public ReportRequestDto(String accountNumber, String startDate, String endDate) {
        this.accountNumber = accountNumber;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "ReportRequestDto{" +
                "clientId=" + clientId +
                ", accountNumber='" + accountNumber + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                '}';
    }
}
