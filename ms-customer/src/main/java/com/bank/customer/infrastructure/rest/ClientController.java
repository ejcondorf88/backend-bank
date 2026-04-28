package com.bank.customer.infrastructure.rest;

import com.bank.customer.application.dto.ClientRequestDto;
import com.bank.customer.application.dto.ClientResponseDto;
import com.bank.customer.application.mapper.ClientDtoMapper;
import com.bank.customer.domain.entity.Client;
import com.bank.customer.domain.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;
    private final ClientDtoMapper clientDtoMapper;

    public ClientController(ClientService clientService, ClientDtoMapper clientDtoMapper) {
        this.clientService = clientService;
        this.clientDtoMapper = clientDtoMapper;
    }

    @PostMapping
    public ResponseEntity<ClientResponseDto> createClient(@Valid @RequestBody ClientRequestDto requestDto) {
        Client client = clientDtoMapper.toDomain(requestDto);
        Client createdClient = clientService.createClient(client);
        ClientResponseDto responseDto = clientDtoMapper.toResponseDto(createdClient);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getClientById(@PathVariable Long id) {
        return clientService.findById(id)
            .map(clientDtoMapper::toResponseDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/identification/{identification}")
    public ResponseEntity<ClientResponseDto> getClientByIdentification(@PathVariable String identification) {
        return clientService.findByIdentification(identification)
            .map(clientDtoMapper::toResponseDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ClientResponseDto>> getAllClients() {
        List<ClientResponseDto> clients = clientService.findAll().stream()
            .map(clientDtoMapper::toResponseDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ClientResponseDto>> getActiveClients() {
        List<ClientResponseDto> clients = clientService.findAllActive().stream()
            .map(clientDtoMapper::toResponseDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(clients);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientResponseDto> updateClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequestDto requestDto) {
        Client client = clientDtoMapper.toDomain(requestDto);
        setClientId(client, id);
        Client updatedClient = clientService.updateClient(client);
        ClientResponseDto responseDto = clientDtoMapper.toResponseDto(updatedClient);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ClientResponseDto> activateClient(@PathVariable Long id) {
        Client activatedClient = clientService.activateClient(id);
        ClientResponseDto responseDto = clientDtoMapper.toResponseDto(activatedClient);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ClientResponseDto> deactivateClient(@PathVariable Long id) {
        Client deactivatedClient = clientService.deactivateClient(id);
        ClientResponseDto responseDto = clientDtoMapper.toResponseDto(deactivatedClient);
        return ResponseEntity.ok(responseDto);
    }

    private void setClientId(Client client, Long id) {
        try {
            java.lang.reflect.Field field = Client.class.getDeclaredField("clientId");
            field.setAccessible(true);
            field.set(client, id);
        } catch (Exception e) {
            throw new RuntimeException("Error setting client id", e);
        }
    }
}