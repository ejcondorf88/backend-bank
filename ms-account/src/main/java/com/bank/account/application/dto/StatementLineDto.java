package com.bank.account.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para cada línea del reporte de estado de cuenta (F4).
 *
 * <p>Formato JSON exacto requerido por el enunciado:
 * <pre>
 * {
 *   "Fecha": "10/2/2022",
 *   "Cliente": "Marianela Montalvo",
 *   "Numero Cuenta": "225487",
 *   "Tipo": "Corriente",
 *   "Saldo Inicial": 100,
 *   "Estado": true,
 *   "Movimiento": 600,
 *   "Saldo Disponible": 700
 * }
 * </pre>
 *
 * <p>Una línea por cada movimiento de cada cuenta del cliente.
 * Si una cuenta no tiene movimientos en el rango, igual aparece
 * con los campos de movimiento en null.
 */
public class StatementLineDto {

    /**
     * Fecha y hora del movimiento.
     */
    @JsonProperty("Fecha")
    private LocalDateTime fecha;

    /**
     * Nombre del cliente propietario de la cuenta.
     * Se resuelve desde la proyección local de eventos de ms-customer.
     */
    @JsonProperty("Cliente")
    private String cliente;

    /**
     * Número de la cuenta bancaria.
     */
    @JsonProperty("Numero Cuenta")
    private String numeroCuenta;

    /**
     * Tipo de cuenta: "Ahorro" o "Corriente".
     */
    @JsonProperty("Tipo")
    private String tipo;

    /**
     * Saldo de la cuenta ANTES del movimiento.
     * Calculado como: saldoDisponible - movimiento.
     */
    @JsonProperty("Saldo Inicial")
    private BigDecimal saldoInicial;

    /**
     * Estado de la cuenta: true=activa, false=inactiva.
     */
    @JsonProperty("Estado")
    private Boolean estado;

    /**
     * Valor del movimiento.
     * Positivo para depósitos, negativo para retiros.
     */
    @JsonProperty("Movimiento")
    private BigDecimal movimiento;

    /**
     * Saldo disponible de la cuenta DESPUÉS del movimiento.
     */
    @JsonProperty("Saldo Disponible")
    private BigDecimal saldoDisponible;

    // ==================== Constructors ====================

    public StatementLineDto() {
    }

    /**
     * Constructor completo para una línea con movimiento.
     */
    public StatementLineDto(LocalDateTime fecha, String cliente, String numeroCuenta,
                            String tipo, BigDecimal saldoInicial, Boolean estado,
                            BigDecimal movimiento, BigDecimal saldoDisponible) {
        this.fecha = fecha;
        this.cliente = cliente;
        this.numeroCuenta = numeroCuenta;
        this.tipo = tipo;
        this.saldoInicial = saldoInicial;
        this.estado = estado;
        this.movimiento = movimiento;
        this.saldoDisponible = saldoDisponible;
    }

    // ==================== Getters & Setters ====================

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public BigDecimal getSaldoInicial() { return saldoInicial; }
    public void setSaldoInicial(BigDecimal saldoInicial) { this.saldoInicial = saldoInicial; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public BigDecimal getMovimiento() { return movimiento; }
    public void setMovimiento(BigDecimal movimiento) { this.movimiento = movimiento; }

    public BigDecimal getSaldoDisponible() { return saldoDisponible; }
    public void setSaldoDisponible(BigDecimal saldoDisponible) { this.saldoDisponible = saldoDisponible; }
}
