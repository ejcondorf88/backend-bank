package com.bank.account.application.service;

import com.bank.account.application.dto.AccountRequestDto;
import com.bank.account.application.dto.AccountResponseDto;
import com.bank.account.application.dto.TransactionRequestDto;
import com.bank.account.application.mapper.AccountApplicationMapper;
import com.bank.account.domain.entity.Account;
import com.bank.account.domain.exception.AccountAlreadyExistsException;
import com.bank.account.domain.exception.AccountNotFoundException;
import com.bank.account.domain.port.out.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for account use cases.
 * Orchestrates domain logic and coordinates between domain and infrastructure layers.
 * Uses DTOs for input/output and delegates to the domain repository.
 */
@Service
@Transactional(readOnly = true)
public class AccountApplicationService {

    private final AccountRepository accountRepository;
    private final AccountApplicationMapper accountMapper;

    public AccountApplicationService(AccountRepository accountRepository, AccountApplicationMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Transactional
    public AccountResponseDto createAccount(AccountRequestDto requestDto) {
        // Check if account number already exists
        if (accountRepository.existsByAccountNumber(requestDto.getAccountNumber())) {
            throw new AccountAlreadyExistsException(requestDto.getAccountNumber());
        }

        // Map DTO to domain entity
        Account account = accountMapper.toDomain(requestDto);

        // Save account
        Account savedAccount = accountRepository.save(account);

        // Return response DTO
        return accountMapper.toResponseDto(savedAccount);
    }

    public AccountResponseDto findByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        return accountMapper.toResponseDto(account);
    }

    public AccountResponseDto findById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
        return accountMapper.toResponseDto(account);
    }

    public List<AccountResponseDto> findAll() {
        return accountRepository.findAll().stream()
                .map(accountMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<AccountResponseDto> findAllActive() {
        return accountRepository.findByActiveTrue().stream()
                .map(accountMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<AccountResponseDto> findByClientId(Long clientId) {
        return accountRepository.findByClientId(clientId).stream()
                .map(accountMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountResponseDto updateAccount(String accountNumber, AccountRequestDto requestDto) {
        // Find existing account
        Account existingAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        // Check if new account number is different and already exists
        if (!accountNumber.equals(requestDto.getAccountNumber()) &&
                accountRepository.existsByAccountNumber(requestDto.getAccountNumber())) {
            throw new AccountAlreadyExistsException(requestDto.getAccountNumber());
        }

        // Update account fields
        Account updatedAccount = accountMapper.updateDomainFromDto(existingAccount, requestDto);

        // Save and return
        Account savedAccount = accountRepository.save(updatedAccount);
        return accountMapper.toResponseDto(savedAccount);
    }

    @Transactional
    public void deleteAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
        accountRepository.deleteById(account.getId());
    }

    @Transactional
    public AccountResponseDto activateAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        account.activate();
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponseDto(savedAccount);
    }

    @Transactional
    public AccountResponseDto deactivateAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        account.deactivate();
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponseDto(savedAccount);
    }

    @Transactional
    public AccountResponseDto deposit(TransactionRequestDto requestDto) {
        Account account = accountRepository.findByAccountNumber(requestDto.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(requestDto.getAccountNumber()));

        account.deposit(requestDto.getAmount());
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponseDto(savedAccount);
    }

    @Transactional
    public AccountResponseDto withdraw(TransactionRequestDto requestDto) {
        Account account = accountRepository.findByAccountNumber(requestDto.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(requestDto.getAccountNumber()));

        account.withdraw(requestDto.getAmount());
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toResponseDto(savedAccount);
    }
}
