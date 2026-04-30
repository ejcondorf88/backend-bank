package com.bank.account.domain.strategy;

import com.bank.account.domain.entity.Account;
import java.math.BigDecimal;

/**
 * Interface para el patron Strategy aplicado a los movimientos bancarios.
 * Define el contrato para las diferentes operaciones que se pueden realizar sobre una cuenta.
 */
public interface MovementStrategy {
    
    /**
     * Ejecuta la logica del movimiento sobre la cuenta.
     * 
     * @param account la cuenta sobre la cual operar
     * @param amount el monto del movimiento
     * @return el saldo resultante después de la operacion
     */
    BigDecimal execute(Account account, BigDecimal amount);
    
    /**
     * Retorna el tipo de movimiento que maneja esta estrategia.
     * Debe coincidir con los valores esperados en el DTO (ej: "Deposito", "Retiro").
     * 
     * @return el nombre del tipo de movimiento
     */
    String getMovementType();
}
