package com.bank.account.infrastructure.rest;

import com.bank.account.application.dto.StatementLineDto;
import com.bank.account.application.service.ReportApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para el reporte de Estado de Cuenta (F4).
 *
 * <p>Implementa el endpoint exacto requerido por el enunciado:
 * <pre>
 *   GET /reportes?fecha=rango fechas
 * </pre>
 *
 * <p>Respuesta JSON — una línea por movimiento en el periodo:
 * <pre>
 * [
 *   {
 *     "Fecha": "2022-02-10T00:00:00",
 *     "Cliente": "Marianela Montalvo",
 *     "Numero Cuenta": "225487",
 *     "Tipo": "Corriente",
 *     "Saldo Inicial": 100,
 *     "Estado": true,
 *     "Movimiento": 600,
 *     "Saldo Disponible": 700
 *   }
 * ]
 * </pre>
 */
@RestController
@RequestMapping("/reportes")
@Tag(name = "Reportes", description = "Reporte de Estado de Cuenta (F4) - filtrado por fecha y cliente")
public class ReportController {

    private final ReportApplicationService reportService;

    public ReportController(ReportApplicationService reportService) {
        this.reportService = reportService;
    }

    /**
     * Genera el reporte de estado de cuenta para un cliente en un rango de fechas.
     *
     * <p>Endpoint principal del enunciado F4:
     * {@code GET /reportes?fechaInicio=yyyy-MM-dd&fechaFin=yyyy-MM-dd&clienteId=X}
     *
     * <p>Alias: también acepta el parámetro {@code fecha} como "fechaInicio" para
     * compatibilidad con el formato del enunciado ({@code /reportes?fecha=rango fechas}).
     */
    @Operation(
            summary = "Reporte de Estado de Cuenta por cliente y rango de fechas (F4)",
            description = "Genera un reporte detallado con cuentas y movimientos de un cliente " +
                    "en un rango de fechas. Una línea JSON por cada movimiento registrado. " +
                    "El nombre del cliente se resuelve desde la proyección local de eventos RabbitMQ."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatementLineDto.class))),
            @ApiResponse(responseCode = "400", description = "Formato de fecha inválido (usar yyyy-MM-dd)"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<StatementLineDto>> getStatementReport(
            @Parameter(description = "ID del cliente", example = "1", required = true)
            @RequestParam Long clienteId,

            @Parameter(description = "Fecha inicio del periodo (yyyy-MM-dd)", example = "2022-01-01", required = true)
            @RequestParam String fechaInicio,

            @Parameter(description = "Fecha fin del periodo (yyyy-MM-dd)", example = "2022-12-31", required = true)
            @RequestParam String fechaFin) {

        List<StatementLineDto> report = reportService.generateReport(clienteId, fechaInicio, fechaFin);
        return ResponseEntity.ok(report);
    }

    /**
     * Reporte global — todos los movimientos en un rango de fechas (sin filtro de cliente).
     * Útil para administración o auditoría.
     *
     * <p>Endpoint: {@code GET /reportes/global?fechaInicio=yyyy-MM-dd&fechaFin=yyyy-MM-dd}
     */
    @Operation(
            summary = "Reporte global de movimientos por rango de fechas",
            description = "Genera un reporte de todos los movimientos en un periodo de tiempo, " +
                    "sin filtrar por cliente. Útil para administración."
    )
    @ApiResponse(responseCode = "200", description = "Reporte global generado",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = StatementLineDto.class)))
    @GetMapping("/global")
    public ResponseEntity<List<StatementLineDto>> getGlobalReport(
            @Parameter(description = "Fecha inicio (yyyy-MM-dd)", example = "2022-01-01", required = true)
            @RequestParam String fechaInicio,

            @Parameter(description = "Fecha fin (yyyy-MM-dd)", example = "2022-12-31", required = true)
            @RequestParam String fechaFin) {

        List<StatementLineDto> report = reportService.generateGlobalReport(fechaInicio, fechaFin);
        return ResponseEntity.ok(report);
    }
}
