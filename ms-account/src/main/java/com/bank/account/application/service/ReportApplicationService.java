package com.bank.account.application.service;

import com.bank.account.application.dto.StatementLineDto;
import com.bank.account.domain.entity.Account;
import com.bank.account.domain.entity.Movement;
import com.bank.account.domain.repository.AccountRepository;
import com.bank.account.domain.repository.MovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio de aplicación para generar reportes de estado de cuenta (F4).
 *
 * <p>Implementa el requisito F4 del enunciado:
 * "Generar un reporte de Estado de cuenta especificando un rango de fechas y cliente."
 *
 * <p>El reporte contiene:
 * <ul>
 *   <li>Cuentas asociadas con sus respectivos saldos</li>
 *   <li>Detalle de movimientos de las cuentas</li>
 *   <li>Una línea por movimiento con el formato exacto del enunciado</li>
 * </ul>
 *
 * <p>El nombre del cliente se resuelve desde la proyección local mantenida
 * por {@link ClientEventHandlerImpl} a partir de eventos de RabbitMQ.
 * Si el cliente no está en la proyección (primer arranque sin eventos),
 * se muestra "Cliente #ID".
 */
@Service
@Transactional(readOnly = true)
public class ReportApplicationService {

    private final AccountRepository accountRepository;
    private final MovementRepository movementRepository;

    /**
     * Proyección local de clientes: clientId → nombre.
     * Se actualiza en tiempo real via RabbitMQ (ms-customer publica eventos).
     * Compartida con ClientEventHandlerImpl a través de inyección.
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
     * Genera el reporte de estado de cuenta para un cliente en un rango de fechas (F4).
     *
     * <p>Endpoint: GET /reportes?fechaInicio=yyyy-MM-dd&fechaFin=yyyy-MM-dd&clienteId=X
     *
     * @param clienteId   ID del cliente
     * @param fechaInicio fecha de inicio (yyyy-MM-dd)
     * @param fechaFin    fecha de fin (yyyy-MM-dd)
     * @return lista de líneas del reporte, una por movimiento
     */
    public List<StatementLineDto> generateReport(Long clienteId, String fechaInicio, String fechaFin) {
        LocalDateTime startDate = LocalDate.parse(fechaInicio).atStartOfDay();
        LocalDateTime endDate   = LocalDate.parse(fechaFin).atTime(LocalTime.MAX);

        // Resolver nombre del cliente desde la proyección local
        String clienteName = clientNameCache.getOrDefault(clienteId, "Cliente #" + clienteId);

        // Obtener todas las cuentas del cliente
        List<Account> accounts = accountRepository.findByClientId(clienteId);

        List<StatementLineDto> reportLines = new ArrayList<>();

        for (Account account : accounts) {
            // Obtener movimientos de esta cuenta en el rango de fechas
            List<Movement> movements = movementRepository
                    .findByAccountNumberAndDateBetween(account.getAccountNumber(), startDate, endDate);

            if (movements.isEmpty()) {
                // Cuenta sin movimientos en el periodo: mostrar línea con datos de cuenta
                StatementLineDto line = new StatementLineDto(
                        null,                       // sin fecha de movimiento
                        clienteName,
                        account.getAccountNumber(),
                        account.getAccountType(),
                        account.getBalance(),       // saldo inicial = saldo actual
                        account.isActive(),
                        null,                       // sin movimiento
                        account.getBalance()        // saldo disponible = saldo actual
                );
                reportLines.add(line);
            } else {
                // Una línea por cada movimiento
                for (Movement movement : movements) {
                    // Saldo inicial = saldo después del movimiento - valor del movimiento
                    BigDecimal saldoInicial = movement.getBalance().subtract(movement.getAmount());

                    StatementLineDto line = new StatementLineDto(
                            movement.getDate(),
                            clienteName,
                            account.getAccountNumber(),
                            account.getAccountType(),
                            saldoInicial,               // saldo ANTES del movimiento
                            account.isActive(),
                            movement.getAmount(),       // valor del movimiento (+ depósito, - retiro)
                            movement.getBalance()       // saldo DESPUÉS del movimiento
                    );
                    reportLines.add(line);
                }
            }
        }

        return reportLines;
    }

    /**
     * Genera el reporte de estado de cuenta para todos los clientes en un rango de fechas.
     * Útil para reportes globales o administrativos.
     *
     * @param fechaInicio fecha de inicio (yyyy-MM-dd)
     * @param fechaFin    fecha de fin (yyyy-MM-dd)
     * @return lista de líneas del reporte
     */
    public List<StatementLineDto> generateGlobalReport(String fechaInicio, String fechaFin) {
        LocalDateTime startDate = LocalDate.parse(fechaInicio).atStartOfDay();
        LocalDateTime endDate   = LocalDate.parse(fechaFin).atTime(LocalTime.MAX);

        List<Movement> allMovements = movementRepository.findByDateBetween(startDate, endDate);
        List<StatementLineDto> reportLines = new ArrayList<>();

        for (Movement movement : allMovements) {
            // Buscar la cuenta para obtener tipo y estado
            Account account = accountRepository
                    .findByAccountNumber(movement.getAccountNumber())
                    .orElse(null);

            if (account == null) continue;

            String clienteName = clientNameCache.getOrDefault(account.getClientId(),
                    "Cliente #" + account.getClientId());

            BigDecimal saldoInicial = movement.getBalance().subtract(movement.getAmount());

            StatementLineDto line = new StatementLineDto(
                    movement.getDate(),
                    clienteName,
                    account.getAccountNumber(),
                    account.getAccountType(),
                    saldoInicial,
                    account.isActive(),
                    movement.getAmount(),
                    movement.getBalance()
            );
            reportLines.add(line);
        }

        return reportLines;
    }
}
