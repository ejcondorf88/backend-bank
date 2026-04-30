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
 * REST Controller for the Account Statement report (F4).
 *
 * <p>Implements the endpoint required by the F4 functionality:
 * <pre>
 *   GET /reports?startDate=yyyy-MM-dd&endDate=yyyy-MM-dd&clientId=X
 * </pre>
 *
 * <p>JSON response — one line per movement in the period:
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
@RequestMapping("/reports")
@Tag(name = "Reports", description = "Account Statement Report (F4) - filtered by date and client")
public class ReportController {

    private final ReportApplicationService reportService;

    public ReportController(ReportApplicationService reportService) {
        this.reportService = reportService;
    }

    /**
     * Generates the account statement report for a client within a date range.
     *
     * <p>Endpoint for F4 requirement:
     * {@code GET /reportes?startDate=yyyy-MM-dd&endDate=yyyy-MM-dd&clientId=X}
     */
    @Operation(
            summary = "Account Statement Report by client and date range (F4)",
            description = "Generates a detailed report with accounts and movements for a client " +
                    "in a date range. Returns one JSON line per recorded movement. " +
                    "Client name is resolved from local event projections."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Report generated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StatementLineDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid date format (use yyyy-MM-dd)"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<List<StatementLineDto>> getStatementReport(
            @Parameter(description = "Unique ID of the client", example = "1", required = true)
            @RequestParam(name = "clientId") Long clientId,

            @Parameter(description = "Range start date (yyyy-MM-dd)", example = "2022-01-01", required = true)
            @RequestParam(name = "startDate") String startDate,

            @Parameter(description = "Range end date (yyyy-MM-dd)", example = "2022-12-31", required = true)
            @RequestParam(name = "endDate") String endDate) {

        List<StatementLineDto> report = reportService.generateReport(clientId, startDate, endDate);
        return ResponseEntity.ok(report);
    }

    /**
     * Global report — all movements in a date range (no client filter).
     * Useful for auditing.
     *
     * <p>Endpoint: {@code GET /reportes/global?startDate=yyyy-MM-dd&endDate=yyyy-MM-dd}
     */
    @Operation(
            summary = "Global movement report by date range",
            description = "Generates a report of all movements in a period, " +
                    "without filtering by client. Useful for administration."
    )
    @ApiResponse(responseCode = "200", description = "Global report generated",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = StatementLineDto.class)))
    @GetMapping("/global")
    public ResponseEntity<List<StatementLineDto>> getGlobalReport(
            @Parameter(description = "Range start date (yyyy-MM-dd)", example = "2022-01-01", required = true)
            @RequestParam(name = "startDate") String startDate,

            @Parameter(description = "Range end date (yyyy-MM-dd)", example = "2022-12-31", required = true)
            @RequestParam(name = "endDate") String endDate) {

        List<StatementLineDto> report = reportService.generateGlobalReport(startDate, endDate);
        return ResponseEntity.ok(report);
    }
}
