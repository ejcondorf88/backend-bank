package com.bank.customer.infrastructure.rest;

import com.bank.customer.application.dto.ClientRequestDto;
import com.bank.customer.application.dto.ClientResponseDto;
import com.bank.customer.application.mapper.ClientApplicationMapper;
import com.bank.customer.domain.entity.Client;
import com.bank.customer.domain.port.in.ClientService;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "Clientes", description = "API para la gestion de clientes del banco")
public class ClientController {

    private final ClientService clientService;
    private final ClientApplicationMapper clientDtoMapper;

    public ClientController(ClientService clientService, ClientApplicationMapper clientDtoMapper) {
        this.clientService = clientService;
        this.clientDtoMapper = clientDtoMapper;
    }

    @Operation(
        summary = "Crear un nuevo cliente",
        description = "Crea un cliente con los datos proporcionados. La identificacion debe ser unica."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ClientResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos invalidos o cliente ya existe"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<ClientResponseDto> createClient(
            @Valid @RequestBody ClientRequestDto requestDto) {
        Client client = clientDtoMapper.toDomain(requestDto);
        Client createdClient = clientService.createClient(client);
        ClientResponseDto responseDto = clientDtoMapper.toResponseDto(createdClient);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(
        summary = "Obtener cliente por ID",
        description = "Recupera los datos de un cliente especifico mediante su ID interno"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ClientResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getClientById(
            @Parameter(description = "ID interno del cliente", example = "1", required = true)
            @PathVariable Long id) {
        Client client = clientService.getById(id);
        return ResponseEntity.ok(clientDtoMapper.toResponseDto(client));
    }

    @Operation(
        summary = "Obtener cliente por identificacion",
        description = "Busca un cliente por su numero de identificacion (cedula, pasaporte, etc.)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ClientResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado con esa identificacion")
    })
    @GetMapping("/identification/{identification}")
    public ResponseEntity<ClientResponseDto> getClientByIdentification(
            @Parameter(description = "Numero de identificacion del cliente", example = "1234567890", required = true)
            @PathVariable String identification) {
        Client client = clientService.getByIdentification(identification);
        return ResponseEntity.ok(clientDtoMapper.toResponseDto(client));
    }

    @Operation(
        summary = "Listar todos los clientes",
        description = "Obtiene la lista completa de todos los clientes registrados"
    )
    @ApiResponse(responseCode = "200", description = "Lista de clientes",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ClientResponseDto.class)))
    @GetMapping
    public ResponseEntity<List<ClientResponseDto>> getAllClients() {
        List<ClientResponseDto> clients = clientService.findAll().stream()
                .map(clientDtoMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clients);
    }

    @Operation(
        summary = "Listar clientes activos",
        description = "Obtiene solo los clientes que se encuentran activos en el sistema"
    )
    @ApiResponse(responseCode = "200", description = "Lista de clientes activos",
        content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ClientResponseDto.class)))
    @GetMapping("/active")
    public ResponseEntity<List<ClientResponseDto>> getActiveClients() {
        List<ClientResponseDto> clients = clientService.findAllActive().stream()
                .map(clientDtoMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clients);
    }

    @Operation(
        summary = "Actualizar cliente",
        description = "Actualiza los datos de un cliente existente. Se requiere el ID del cliente."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente actualizado exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ClientResponseDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos invalidos"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> updateClient(
            @Parameter(description = "ID interno del cliente a actualizar", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ClientRequestDto requestDto) {
        Client client = clientDtoMapper.toDomainForUpdate(id, requestDto);
        Client updatedClient = clientService.updateClient(client);
        ClientResponseDto responseDto = clientDtoMapper.toResponseDto(updatedClient);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
        summary = "Eliminar cliente",
        description = "Elimina permanentemente un cliente del sistema. Esta accion no se puede deshacer."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cliente eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(
            @Parameter(description = "ID interno del cliente a eliminar", example = "1", required = true)
            @PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Activar cliente",
        description = "Cambia el estado de un cliente a activo"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente activado exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ClientResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "400", description = "El cliente ya esta activo")
    })
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ClientResponseDto> activateClient(
            @Parameter(description = "ID interno del cliente a activar", example = "1", required = true)
            @PathVariable Long id) {
        Client activatedClient = clientService.activateClient(id);
        ClientResponseDto responseDto = clientDtoMapper.toResponseDto(activatedClient);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(
        summary = "Desactivar cliente",
        description = "Cambia el estado de un cliente a inactivo"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente desactivado exitosamente",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ClientResponseDto.class))),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
        @ApiResponse(responseCode = "400", description = "El cliente ya esta inactivo")
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ClientResponseDto> deactivateClient(
            @Parameter(description = "ID interno del cliente a desactivar", example = "1", required = true)
            @PathVariable Long id) {
        Client deactivatedClient = clientService.deactivateClient(id);
        ClientResponseDto responseDto = clientDtoMapper.toResponseDto(deactivatedClient);
        return ResponseEntity.ok(responseDto);
    }
}
