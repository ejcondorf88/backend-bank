package com.bank.account.infrastructure.persistence;

import com.bank.account.domain.entity.Account;
import com.bank.account.domain.repository.AccountRepository;
import com.bank.account.infrastructure.persistence.entity.AccountJpaEntity;
import com.bank.account.infrastructure.persistence.mapper.AccountJpaMapper;
import com.bank.account.infrastructure.persistence.repository.AccountJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the domain AccountRepository interface.
 * Bridges the domain layer with the infrastructure JPA repository.
 */
@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountJpaRepository jpaRepository;
    private final AccountJpaMapper jpaMapper;

    public AccountRepositoryImpl(AccountJpaRepository jpaRepository, AccountJpaMapper jpaMapper) {
        this.jpaRepository = jpaRepository;
        this.jpaMapper = jpaMapper;
    }

    @Override
    public Account save(Account account) {
        AccountJpaEntity jpaEntity = jpaMapper.toJpaEntity(account);
        AccountJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return jpaMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Account> findById(Long id) {
        return jpaRepository.findById(id)
                .map(jpaMapper::toDomain);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return jpaRepository.findByAccountNumber(accountNumber)
                .map(jpaMapper::toDomain);
    }

    @Override
    public List<Account> findAll() {
        return jpaRepository.findAll().stream()
                .map(jpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> findByClientId(Long clientId) {
        return jpaRepository.findByClientId(clientId).stream()
                .map(jpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Account> findByActiveTrue() {
        return jpaRepository.findByActiveTrue().stream()
                .map(jpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return jpaRepository.existsByAccountNumber(accountNumber);
    }
}
