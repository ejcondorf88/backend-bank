package com.bank.account.domain.strategy;

import com.bank.account.domain.entity.Account;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * Estrategia para procesar depositos.
 */
@Component
public class DepositStrategy implements MovementStrategy {

    @Override
    public BigDecimal execute(Account account, BigDecimal amount) {
        account.deposit(amount);
        return account.getBalance();
    }

    @Override
    public String getMovementType() {
        return "Deposito";
    }
}
