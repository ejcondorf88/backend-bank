package com.bank.account.domain.strategy;

import com.bank.account.domain.entity.Account;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

/**
 * Estrategia para procesar retiros.
 */
@Component
public class WithdrawalStrategy implements MovementStrategy {

    @Override
    public BigDecimal execute(Account account, BigDecimal amount) {
        account.withdraw(amount);
        return account.getBalance();
    }

    @Override
    public String getMovementType() {
        return "Retiro";
    }
}
