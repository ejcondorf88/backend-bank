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
    private String fechaInicio;

    @NotNull(message = "End date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "End date must be in format yyyy-MM-dd")
    private String fechaFin;

    // Default constructor
    public ReportRequestDto() {
    }

    // Constructor with all fields
    public ReportRequestDto(String fechaInicio, String fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Constructor with client
    public ReportRequestDto(Long clientId, String fechaInicio, String fechaFin) {
        this.clientId = clientId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    // Constructor with account
    public ReportRequestDto(String accountNumber, String fechaInicio, String fechaFin) {
        this.accountNumber = accountNumber;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
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

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    @Override
    public String toString() {
        return "ReportRequestDto{" +
                "clientId=" + clientId +
                ", accountNumber='" + accountNumber + '\'' +
                ", fechaInicio='" + fechaInicio + '\'' +
                ", fechaFin='" + fechaFin + '\'' +
                '}';
    }
}
