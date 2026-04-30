package com.bank.account.application.service;

import com.bank.account.application.dto.MovementRequestDto;
import com.bank.account.application.dto.MovementResponseDto;
import com.bank.account.application.mapper.MovementApplicationMapper;
import com.bank.account.domain.entity.Account;
import com.bank.account.domain.entity.Movement;
import com.bank.account.domain.exception.AccountNotFoundException;
import com.bank.account.domain.exception.InsufficientBalanceException;
import com.bank.account.domain.exception.InvalidAccountStateException;
import com.bank.account.domain.exception.MovementNotFoundException;
import com.bank.account.domain.port.out.AccountRepository;
import com.bank.account.domain.port.out.MovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service for movement use cases.
 * Orchestrates domain logic and coordinates between domain and infrastructure layers.
 * Implements F2 requirement: Register and retrieve movements.
 */
@Service
@Transactional(readOnly = true)
public class MovementApplicationService {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;
    private final MovementApplicationMapper movementMapper;

    public MovementApplicationService(MovementRepository movementRepository,
                                      AccountRepository accountRepository,
                                      MovementApplicationMapper movementMapper) {
        this.movementRepository = movementRepository;
        this.accountRepository = accountRepository;
        this.movementMapper = movementMapper;
    }

    /**
     * Creates a deposit movement.
     * F2: Registers a deposit transaction.
     *
     * @param accountNumber the account number
     * @param amount the deposit amount
     * @return the created movement response
     */
    @Transactional
    public MovementResponseDto createDeposit(String accountNumber, BigDecimal amount) {
        // Find account
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        // Execute deposit
        account.deposit(amount);

        // Calculate balance after
        BigDecimal balanceAfter = account.getBalance();

        // Create movement
        Movement movement = Movement.createDeposit(accountNumber, amount, balanceAfter);
        Movement savedMovement = movementRepository.save(movement);

        // Save account
        accountRepository.save(account);

        return movementMapper.toResponseDto(savedMovement);
    }

    /**
     * Creates a withdrawal movement.
     * F2: Registers a withdrawal transaction.
     *
     * @param accountNumber the account number
     * @param amount the withdrawal amount
     * @return the created movement response
     */
    @Transactional
    public MovementResponseDto createWithdrawal(String accountNumber, BigDecimal amount) {
        // Find account
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        // Execute withdrawal (may throw InsufficientBalanceException)
        account.withdraw(amount);

        // Calculate balance after
        BigDecimal balanceAfter = account.getBalance();

        // Create movement
        Movement movement = Movement.createWithdrawal(accountNumber, amount, balanceAfter);
        Movement savedMovement = movementRepository.save(movement);

        // Save account
        accountRepository.save(account);

        return movementMapper.toResponseDto(savedMovement);
    }

    /**
     * Creates a movement from request DTO.
     *
     * @param requestDto the movement request
     * @return the created movement response
     */
    @Transactional
    public MovementResponseDto createMovement(MovementRequestDto requestDto) {
        // Find account
        Account account = accountRepository.findByAccountNumber(requestDto.getAccountNumber())
                .orElseThrow(() -> new AccountNotFoundException(requestDto.getAccountNumber()));

        // Validate account is active
        if (!account.isActive()) {
            throw new InvalidAccountStateException("Cannot create movement on inactive account");
        }

        BigDecimal balanceAfter;

        // Execute transaction on account
        if ("Deposito".equalsIgnoreCase(requestDto.getType())) {
            account.deposit(requestDto.getAmount());
            balanceAfter = account.getBalance();
        } else if ("Retiro".equalsIgnoreCase(requestDto.getType())) {
            account.withdraw(requestDto.getAmount());
            balanceAfter = account.getBalance();
        } else {
            throw new IllegalArgumentException("Invalid movement type: " + requestDto.getType());
        }

        // Create movement
        Movement movement = movementMapper.fromTransaction(
                requestDto.getAccountNumber(),
                requestDto.getType(),
                requestDto.getAmount(),
                balanceAfter
        );
        Movement savedMovement = movementRepository.save(movement);

        // Save account
        accountRepository.save(account);

        return movementMapper.toResponseDto(savedMovement);
    }

    /**
     * Finds a movement by ID.
     *
     * @param id the movement ID
     * @return the movement response
     */
    public MovementResponseDto findById(Long id) {
        Movement movement = movementRepository.findById(id)
                .orElseThrow(() -> new MovementNotFoundException(id));
        return movementMapper.toResponseDto(movement);
    }

    /**
     * Finds all movements.
     *
     * @return list of movement responses
     */
    public List<MovementResponseDto> findAll() {
        return movementRepository.findAll().stream()
                .map(movementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds movements by account number.
     *
     * @param accountNumber the account number
     * @return list of movement responses
     */
    public List<MovementResponseDto> findByAccountNumber(String accountNumber) {
        return movementRepository.findByAccountNumber(accountNumber).stream()
                .map(movementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds movements by client ID.
     * F4: Support for client reports.
     *
     * @param clientId the client ID
     * @return list of movement responses
     */
    public List<MovementResponseDto> findByClientId(Long clientId) {
        return movementRepository.findByClientId(clientId).stream()
                .map(movementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds movements by account and date range.
     * F4: Support for date range reports.
     *
     * @param accountNumber the account number
     * @param fechaInicio start date (yyyy-MM-dd)
     * @param fechaFin end date (yyyy-MM-dd)
     * @return list of movement responses
     */
    public List<MovementResponseDto> findByAccountAndDateRange(String accountNumber, String fechaInicio, String fechaFin) {
        LocalDateTime startDate = LocalDate.parse(fechaInicio).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(fechaFin).atTime(LocalTime.MAX);

        return movementRepository.findByAccountNumberAndDateBetween(accountNumber, startDate, endDate).stream()
                .map(movementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds movements by date range.
     * F4: Support for general date range reports.
     *
     * @param fechaInicio start date (yyyy-MM-dd)
     * @param fechaFin end date (yyyy-MM-dd)
     * @return list of movement responses
     */
    public List<MovementResponseDto> findByDateRange(String fechaInicio, String fechaFin) {
        LocalDateTime startDate = LocalDate.parse(fechaInicio).atStartOfDay();
        LocalDateTime endDate = LocalDate.parse(fechaFin).atTime(LocalTime.MAX);

        return movementRepository.findByDateBetween(startDate, endDate).stream()
                .map(movementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a movement by ID.
     *
     * @param id the movement ID
     */
    @Transactional
    public void deleteMovement(Long id) {
        Movement movement = movementRepository.findById(id)
                .orElseThrow(() -> new MovementNotFoundException(id));
        movementRepository.deleteById(movement.getId());
    }

    /**
     * Gets movement statistics for an account.
     *
     * @param accountNumber the account number
     * @return the total number of movements
     */
    public long getMovementCount(String accountNumber) {
        return movementRepository.countByAccountNumber(accountNumber);
    }
}
