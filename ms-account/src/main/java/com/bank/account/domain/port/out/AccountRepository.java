package com.bank.account.domain.port.out;

import com.bank.account.domain.entity.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(Long id);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findAll();

    List<Account> findByClientId(Long clientId);

    List<Account> findByActiveTrue();

    void deleteById(Long id);

    boolean existsByAccountNumber(String accountNumber);
}