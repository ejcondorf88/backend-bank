package com.bank.account.infrastructure.rest;

import com.bank.account.application.dto.MovementRequestDto;
import com.bank.account.application.dto.MovementResponseDto;
import com.bank.account.application.dto.ReportRequestDto;
import com.bank.account.application.dto.ReportResponseDto;
import com.bank.account.application.service.MovementApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/movements")
@Tag(name = "Movimientos", description = "API para la gestion de movimientos/transacciones bancarias")
public class MovementController {

    private final MovementApplicationService movementService;

    public MovementController(MovementApplicationService movementService) {
        this.movementService = movementService;
    }

    @Operation(
            summary = "Crear un nuevo movimiento",
            description = "Crea un movimiento (deposito o retiro) y actualiza el saldo de la cuenta. " +
                    "Para depositos use tipo 'Deposito', para retiros use tipo 'Retiro'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Movimiento creado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovementResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos, tipo no valido o cuenta inactiva"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<MovementResponseDto> createMovement(
            @Valid @RequestBody MovementRequestDto requestDto) {
        MovementResponseDto responseDto = movementService.createMovement(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(
            summary = "Realizar deposito",
            description = "Realiza un deposito en una cuenta. El monto debe ser positivo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Deposito realizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovementResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Monto invalido o cuenta inactiva"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<MovementResponseDto> createDeposit(
            @Parameter(description = "Numero de cuenta", example = "478758", required = true)
            @PathVariable String accountNumber,
            @Valid @RequestBody BigDecimal amount) {
        MovementResponseDto responseDto = movementService.createDeposit(accountNumber, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(
            summary = "Realizar retiro",
            description = "Realiza un retiro de una cuenta. Valida que haya saldo suficiente (F3)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Retiro realizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovementResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Saldo insuficiente (F3), cuenta inactiva o monto invalido"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<MovementResponseDto> createWithdrawal(
            @Parameter(description = "Numero de cuenta", example = "478758", required = true)
            @PathVariable String accountNumber,
            @Valid @RequestBody BigDecimal amount) {
        MovementResponseDto responseDto = movementService.createWithdrawal(accountNumber, amount);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(
            summary = "Obtener movimiento por ID",
            description = "Recupera los datos de un movimiento especifico mediante su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovementResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MovementResponseDto> getMovementById(
            @Parameter(description = "ID del movimiento", example = "1", required = true)
            @PathVariable Long id) {
        MovementResponseDto responseDto = movementService.findById(id);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "Listar todos los movimientos",
            description = "Obtiene la lista completa de todos los movimientos registrados"
    )
    @ApiResponse(responseCode = "200", description = "Lista de movimientos",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = MovementResponseDto.class)))
    @GetMapping
    public ResponseEntity<List<MovementResponseDto>> getAllMovements() {
        List<MovementResponseDto> movements = movementService.findAll();
        return ResponseEntity.ok(movements);
    }

    @Operation(
            summary = "Listar movimientos de una cuenta",
            description = "Obtiene todos los movimientos asociados a una cuenta especifica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de movimientos",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovementResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Numero de cuenta invalido")
    })
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<MovementResponseDto>> getMovementsByAccount(
            @Parameter(description = "Numero de cuenta", example = "478758", required = true)
            @PathVariable String accountNumber) {
        List<MovementResponseDto> movements = movementService.findByAccountNumber(accountNumber);
        return ResponseEntity.ok(movements);
    }

    @Operation(
            summary = "Listar movimientos por cliente",
            description = "Obtiene todos los movimientos de todas las cuentas de un cliente (F4)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de movimientos del cliente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovementResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "ID de cliente invalido")
    })
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<MovementResponseDto>> getMovementsByClient(
            @Parameter(description = "ID del cliente", example = "1", required = true)
            @PathVariable Long clientId) {
        List<MovementResponseDto> movements = movementService.findByClientId(clientId);
        return ResponseEntity.ok(movements);
    }

    @Operation(
            summary = "Reporte de movimientos por rango de fechas",
            description = "Genera un reporte de movimientos filtrado por fechas (F4). " +
                    "Formato de fecha: yyyy-MM-dd"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovementResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Rango de fechas invalido")
    })
    @GetMapping("/report")
    public ResponseEntity<List<MovementResponseDto>> getMovementsByDateRange(
            @Parameter(description = "Fecha inicio (yyyy-MM-dd)", example = "2022-02-01", required = true)
            @RequestParam String fechaInicio,
            @Parameter(description = "Fecha fin (yyyy-MM-dd)", example = "2022-02-15", required = true)
            @RequestParam String fechaFin) {
        List<MovementResponseDto> movements = movementService.findByDateRange(fechaInicio, fechaFin);
        return ResponseEntity.ok(movements);
    }

    @Operation(
            summary = "Reporte de movimientos por cuenta y rango de fechas",
            description = "Genera un reporte de movimientos para una cuenta especifica filtrado por fechas (F4)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MovementResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Rango de fechas invalido o cuenta no existe"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/report/{accountNumber}")
    public ResponseEntity<List<MovementResponseDto>> getMovementsByAccountAndDateRange(
            @Parameter(description = "Numero de cuenta", example = "478758", required = true)
            @PathVariable String accountNumber,
            @Parameter(description = "Fecha inicio (yyyy-MM-dd)", example = "2022-02-01", required = true)
            @RequestParam String fechaInicio,
            @Parameter(description = "Fecha fin (yyyy-MM-dd)", example = "2022-02-15", required = true)
            @RequestParam String fechaFin) {
        List<MovementResponseDto> movements = movementService.findByAccountAndDateRange(accountNumber, fechaInicio, fechaFin);
        return ResponseEntity.ok(movements);
    }

    @Operation(
            summary = "Eliminar movimiento",
            description = "Elimina permanentemente un movimiento del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movimiento eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMovement(
            @Parameter(description = "ID del movimiento a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        movementService.deleteMovement(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Contar movimientos de una cuenta",
            description = "Retorna el numero total de movimientos registrados para una cuenta"
    )
    @ApiResponse(responseCode = "200", description = "Conteo exitoso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Long.class)))
    @GetMapping("/count/{accountNumber}")
    public ResponseEntity<Long> countMovementsByAccount(
            @Parameter(description = "Numero de cuenta", example = "478758", required = true)
            @PathVariable String accountNumber) {
        long count = movementService.getMovementCount(accountNumber);
        return ResponseEntity.ok(count);
    }
}
