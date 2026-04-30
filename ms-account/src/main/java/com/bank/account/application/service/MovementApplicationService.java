package com.bank.account.application.service;

import com.bank.account.application.dto.MovementRequestDto;
import com.bank.account.application.dto.MovementResponseDto;
import com.bank.account.application.mapper.MovementApplicationMapper;
import com.bank.account.domain.entity.Account;
import com.bank.account.domain.entity.Movement;
import com.bank.account.domain.exception.AccountNotFoundException;
import com.bank.account.domain.exception.InvalidAccountStateException;
import com.bank.account.domain.exception.MovementNotFoundException;
import com.bank.account.domain.port.out.AccountRepository;
import com.bank.account.domain.port.out.MovementRepository;
import com.bank.account.domain.strategy.MovementStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Application service for movement use cases.
 * Orchestrates domain logic and coordinates between domain and infrastructure layers.
 * Implements F2 requirement: Register and retrieve movements.
 * Uses Strategy Pattern to handle different types of movements.
 */
@Service
@Transactional(readOnly = true)
public class MovementApplicationService {

    private final MovementRepository movementRepository;
    private final AccountRepository accountRepository;
    private final MovementApplicationMapper movementMapper;
    private final Map<String, MovementStrategy> strategies;

    public MovementApplicationService(MovementRepository movementRepository,
                                      AccountRepository accountRepository,
                                      MovementApplicationMapper movementMapper,
                                      List<MovementStrategy> movementStrategies) {
        this.movementRepository = movementRepository;
        this.accountRepository = accountRepository;
        this.movementMapper = movementMapper;
        this.strategies = movementStrategies.stream()
                .collect(Collectors.toMap(
                        s -> s.getMovementType().toLowerCase(),
                        s -> s
                ));
    }

    /**
     * Creates a deposit movement.
     * F2: Registers a deposit transaction using the Strategy Pattern.
     *
     * @param accountNumber the account number
     * @param amount the deposit amount
     * @return the created movement response
     */
    @Transactional
    public MovementResponseDto createDeposit(String accountNumber, BigDecimal amount) {
        return processMovement(accountNumber, "Deposito", amount);
    }

    /**
     * Creates a withdrawal movement.
     * F2: Registers a withdrawal transaction using the Strategy Pattern.
     *
     * @param accountNumber the account number
     * @param amount the withdrawal amount
     * @return the created movement response
     */
    @Transactional
    public MovementResponseDto createWithdrawal(String accountNumber, BigDecimal amount) {
        return processMovement(accountNumber, "Retiro", amount);
    }

    /**
     * Creates a movement from request DTO.
     *
     * @param requestDto the movement request
     * @return the created movement response
     */
    @Transactional
    public MovementResponseDto createMovement(MovementRequestDto requestDto) {
        return processMovement(requestDto.getAccountNumber(), requestDto.getType(), requestDto.getAmount());
    }

    /**
     * Internal method to process any type of movement using strategies.
     */
    private MovementResponseDto processMovement(String accountNumber, String type, BigDecimal amount) {
        // Find account
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

        // Validate account is active
        if (!account.isActive()) {
            throw new InvalidAccountStateException("Cannot create movement on inactive account");
        }

        // Find strategy
        MovementStrategy strategy = strategies.get(type.toLowerCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Invalid movement type: " + type);
        }

        // Execute strategy
        BigDecimal balanceAfter = strategy.execute(account, amount);

        // Create movement record
        Movement movement = movementMapper.fromTransaction(
                accountNumber,
                strategy.getMovementType(),
                amount,
                balanceAfter
        );
        Movement savedMovement = movementRepository.save(movement);

        // Save account state
        accountRepository.save(account);

        return movementMapper.toResponseDto(savedMovement);
    }

    /**
     * Finds a movement by ID.
     */
    public MovementResponseDto findById(Long id) {
        Movement movement = movementRepository.findById(id)
                .orElseThrow(() -> new MovementNotFoundException(id));
        return movementMapper.toResponseDto(movement);
    }

    /**
     * Finds all movements.
     */
    public List<MovementResponseDto> findAll() {
        return movementRepository.findAll().stream()
                .map(movementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds movements by account number.
     */
    public List<MovementResponseDto> findByAccountNumber(String accountNumber) {
        return movementRepository.findByAccountNumber(accountNumber).stream()
                .map(movementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds movements by client ID.
     */
    public List<MovementResponseDto> findByClientId(Long clientId) {
        return movementRepository.findByClientId(clientId).stream()
                .map(movementMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Finds movements by account and date range.
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
     */
    @Transactional
    public void deleteMovement(Long id) {
        Movement movement = movementRepository.findById(id)
                .orElseThrow(() -> new MovementNotFoundException(id));
        movementRepository.deleteById(movement.getId());
    }

    /**
     * Gets movement statistics for an account.
     */
    public long getMovementCount(String accountNumber) {
        return movementRepository.countByAccountNumber(accountNumber);
    }
}

