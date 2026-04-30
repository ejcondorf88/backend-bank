package com.bank.account.application.service;

import com.bank.account.application.dto.StatementLineDto;
import com.bank.account.domain.entity.Account;
import com.bank.account.domain.entity.Movement;
import com.bank.account.domain.port.out.AccountRepository;
import com.bank.account.domain.port.out.MovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Application service for generating Account Statement reports (F4).
 *
 * <p>Implements the F4 requirement:
 * "Generate an Account Statement report specifying a date range and client."
 *
 * <p>The report includes:
 * <ul>
 *   <li>Associated accounts with their respective balances</li>
 *   <li>Detailed movements for each account</li>
 *   <li>One JSON line per movement following the exact exam format</li>
 * </ul>
 *
 * <p>The client name is resolved from the local projection maintained 
 * by {@link ClientEventHandlerImpl} via RabbitMQ events from ms-customer.
 */
@Service
@Transactional(readOnly = true)
public class ReportApplicationService {

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;

    /**
     * Local client projection: clientId -> name.
     * Updated in real-time via RabbitMQ events.
     */
    private final Map<Long, String> clientNameCache;

    public ReportApplicationService(AccountRepository accountRepository,
                                    MovementRepository movementRepository,
                                    Map<Long, String> clientNameCache) {
        this.accountRepository = accountRepository;
        this.movementRepository = movementRepository;
        this.clientNameCache = clientNameCache;
    }

    /**
     * Generates the account statement report for a client within a date range (F4).
     *
     * @param clientId   The client's unique ID
     * @param startDate  Range start date (yyyy-MM-dd)
     * @param endDate    Range end date (yyyy-MM-dd)
     * @return List of statement lines, one per movement
     */
    public List<StatementLineDto> generateReport(Long clientId, String startDate, String endDate) {
        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime   = LocalDate.parse(endDate).atTime(LocalTime.MAX);

        // Resolve client name from local projection cache
        String clientName = clientNameCache.getOrDefault(clientId, "Client #" + clientId);

        // Retrieve all accounts for this client
        List<Account> accounts = accountRepository.findByClientId(clientId);

        List<StatementLineDto> reportLines = new ArrayList<>();

        for (Account account : accounts) {
            // Get movements for this account in the date range
            List<Movement> movements = movementRepository
                    .findByAccountNumberAndDateBetween(account.getAccountNumber(), startDateTime, endDateTime);

            if (movements.isEmpty()) {
                // Account with no movements: show a single line with current account data
                StatementLineDto line = new StatementLineDto(
                        null,                       // no movement date
                        clientName,
                        account.getAccountNumber(),
                        account.getAccountType(),
                        account.getBalance(),       // initial balance = current balance
                        account.isActive(),
                        null,                       // no movement amount
                        account.getBalance()        // available balance = current balance
                );
                reportLines.add(line);
            } else {
                // One line for each recorded movement
                for (Movement movement : movements) {
                    // Initial balance = balance after movement - movement amount
                    BigDecimal initialBalanceBefore = movement.getBalance().subtract(movement.getAmount());

                    StatementLineDto line = new StatementLineDto(
                            movement.getDate(),
                            clientName,
                            account.getAccountNumber(),
                            account.getAccountType(),
                            initialBalanceBefore,       // Balance BEFORE movement
                            account.isActive(),
                            movement.getAmount(),       // Movement value (+ deposit, - withdrawal)
                            movement.getBalance()       // Balance AFTER movement
                    );
                    reportLines.add(line);
                }
            }
        }

        return reportLines;
    }

    /**
     * Generates a global report for all clients within a date range.
     * Useful for administrative or auditing purposes.
     *
     * @param startDate  Range start date (yyyy-MM-dd)
     * @param endDate    Range end date (yyyy-MM-dd)
     * @return List of statement lines
     */
    public List<StatementLineDto> generateGlobalReport(String startDate, String endDate) {
        LocalDateTime startDateTime = LocalDate.parse(startDate).atStartOfDay();
        LocalDateTime endDateTime   = LocalDate.parse(endDate).atTime(LocalTime.MAX);

        List<Movement> allMovements = movementRepository.findByDateBetween(startDateTime, endDateTime);
        List<StatementLineDto> reportLines = new ArrayList<>();

        for (Movement movement : allMovements) {
            // Find account to get type and status
            Account account = accountRepository
                    .findByAccountNumber(movement.getAccountNumber())
                    .orElse(null);

            if (account == null) continue;

            String clientName = clientNameCache.getOrDefault(account.getClientId(),
                    "Client #" + account.getClientId());

            BigDecimal initialBalanceBefore = movement.getBalance().subtract(movement.getAmount());

            StatementLineDto line = new StatementLineDto(
                    movement.getDate(),
                    clientName,
                    account.getAccountNumber(),
                    account.getAccountType(),
                    initialBalanceBefore,
                    account.isActive(),
                    movement.getAmount(),
                    movement.getBalance()
            );
            reportLines.add(line);
        }

        return reportLines;
    }
}
