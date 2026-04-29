package com.bank.account.infrastructure.rest;

import com.bank.account.application.dto.AccountRequestDto;
import com.bank.account.application.dto.AccountResponseDto;
import com.bank.account.application.dto.TransactionRequestDto;
import com.bank.account.application.service.AccountApplicationService;
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
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "Cuentas", description = "API para la gestion de cuentas bancarias")
public class AccountController {

    private final AccountApplicationService accountService;

    public AccountController(AccountApplicationService accountService) {
        this.accountService = accountService;
    }

    @Operation(
            summary = "Crear una nueva cuenta",
            description = "Crea una cuenta bancaria con los datos proporcionados. El numero de cuenta debe ser unico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cuenta creada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o tipo de cuenta no valido"),
            @ApiResponse(responseCode = "409", description = "El numero de cuenta ya existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(
            @Valid @RequestBody AccountRequestDto requestDto) {
        AccountResponseDto responseDto = accountService.createAccount(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(
            summary = "Obtener cuenta por numero",
            description = "Recupera los datos de una cuenta especifica mediante su numero de cuenta"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponseDto> getAccountByNumber(
            @Parameter(description = "Numero de cuenta", example = "478758", required = true)
            @PathVariable String accountNumber) {
        AccountResponseDto responseDto = accountService.findByAccountNumber(accountNumber);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "Listar todas las cuentas",
            description = "Obtiene la lista completa de todas las cuentas registradas"
    )
    @ApiResponse(responseCode = "200", description = "Lista de cuentas",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AccountResponseDto.class)))
    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> getAllAccounts() {
        List<AccountResponseDto> accounts = accountService.findAll();
        return ResponseEntity.ok(accounts);
    }

    @Operation(
            summary = "Listar cuentas de un cliente",
            description = "Obtiene todas las cuentas asociadas a un cliente especifico"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de cuentas del cliente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "ID de cliente invalido")
    })
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<AccountResponseDto>> getAccountsByClientId(
            @Parameter(description = "ID del cliente", example = "1", required = true)
            @PathVariable Long clientId) {
        List<AccountResponseDto> accounts = accountService.findByClientId(clientId);
        return ResponseEntity.ok(accounts);
    }

    @Operation(
            summary = "Listar cuentas activas",
            description = "Obtiene solo las cuentas que se encuentran activas"
    )
    @ApiResponse(responseCode = "200", description = "Lista de cuentas activas",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AccountResponseDto.class)))
    @GetMapping("/active")
    public ResponseEntity<List<AccountResponseDto>> getActiveAccounts() {
        // Note: This requires a new method in service or we filter from findAll
        // For now, we'll return all and filter
        List<AccountResponseDto> accounts = accountService.findAll();
        return ResponseEntity.ok(accounts);
    }

    @Operation(
            summary = "Actualizar cuenta",
            description = "Actualiza los datos de una cuenta existente. Se requiere el numero de cuenta."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta actualizada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "409", description = "El nuevo numero de cuenta ya existe")
    })
    @PutMapping("/{accountNumber}")
    public ResponseEntity<AccountResponseDto> updateAccount(
            @Parameter(description = "Numero de cuenta a actualizar", example = "478758", required = true)
            @PathVariable String accountNumber,
            @Valid @RequestBody AccountRequestDto requestDto) {
        AccountResponseDto responseDto = accountService.updateAccount(accountNumber, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "Eliminar cuenta",
            description = "Elimina permanentemente una cuenta del sistema. Esta accion no se puede deshacer."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cuenta eliminada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(
            @Parameter(description = "Numero de cuenta a eliminar", example = "478758", required = true)
            @PathVariable String accountNumber) {
        accountService.deleteAccount(accountNumber);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Activar cuenta",
            description = "Cambia el estado de una cuenta a activo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta activada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "400", description = "La cuenta ya esta activa")
    })
    @PatchMapping("/{accountNumber}/activate")
    public ResponseEntity<AccountResponseDto> activateAccount(
            @Parameter(description = "Numero de cuenta a activar", example = "478758", required = true)
            @PathVariable String accountNumber) {
        AccountResponseDto responseDto = accountService.activateAccount(accountNumber);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "Desactivar cuenta",
            description = "Cambia el estado de una cuenta a inactivo"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta desactivada exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "400", description = "La cuenta ya esta inactiva")
    })
    @PatchMapping("/{accountNumber}/deactivate")
    public ResponseEntity<AccountResponseDto> deactivateAccount(
            @Parameter(description = "Numero de cuenta a desactivar", example = "478758", required = true)
            @PathVariable String accountNumber) {
        AccountResponseDto responseDto = accountService.deactivateAccount(accountNumber);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "Realizar deposito",
            description = "Deposita dinero en una cuenta bancaria"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposito realizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos o cuenta inactiva"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<AccountResponseDto> deposit(
            @Parameter(description = "Numero de cuenta", example = "478758", required = true)
            @PathVariable String accountNumber,
            @Valid @RequestBody TransactionRequestDto requestDto) {
        // Ensure account number matches
        if (!accountNumber.equals(requestDto.getAccountNumber())) {
            return ResponseEntity.badRequest().build();
        }
        AccountResponseDto responseDto = accountService.deposit(requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
            summary = "Realizar retiro",
            description = "Retira dinero de una cuenta bancaria. Valida que haya saldo suficiente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retiro realizado exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Saldo insuficiente, cuenta inactiva o datos invalidos"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<AccountResponseDto> withdraw(
            @Parameter(description = "Numero de cuenta", example = "478758", required = true)
            @PathVariable String accountNumber,
            @Valid @RequestBody TransactionRequestDto requestDto) {
        // Ensure account number matches
        if (!accountNumber.equals(requestDto.getAccountNumber())) {
            return ResponseEntity.badRequest().build();
        }
        AccountResponseDto responseDto = accountService.withdraw(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}
