package com.bank.customer.application.service;

import com.bank.customer.domain.entity.Client;
import com.bank.customer.domain.event.DomainEventPublisher;
import com.bank.customer.domain.exception.ClientAlreadyExistsException;
import com.bank.customer.domain.exception.ClientNotFoundException;
import com.bank.customer.domain.exception.InvalidClientStateException;
import com.bank.customer.domain.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de la capa de aplicación - ClientServiceImpl
 * Usa Mockito para simular el repositorio y el publicador de eventos
 * Solo prueba lógica de aplicación, no infraestructura
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - ClientServiceImpl")
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private DomainEventPublisher eventPublisher;

    private ClientServiceImpl clientService;

    @BeforeEach
    void setUp() {
        clientService = new ClientServiceImpl(clientRepository, eventPublisher);
    }

    private Client createTestClient(Long id, String identification, boolean active) {
        Client client = new Client(
            "Jose Lema",
            "Masculino",
            35,
            identification,
            "Otavalo sn y principal",
            "098254785",
            "1234",
            active
        );
        client.setId(id);
        return client;
    }

    @Test
    @DisplayName("Debe crear cliente cuando no existe")
    void shouldCreateClientWhenNotExists() {
        // Given
        Client newClient = createTestClient(null, "1720456325", true);
        when(clientRepository.existsByIdentification("1720456325")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(createTestClient(1L, "1720456325", true));

        // When
        Client result = clientService.createClient(newClient);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("1720456325", result.getIdentification());
        verify(clientRepository).existsByIdentification("1720456325");
        verify(clientRepository).save(newClient);
    }

    @Test
    @DisplayName("Debe lanzar excepcion cuando cliente ya existe")
    void shouldThrowExceptionWhenClientAlreadyExists() {
        // Given
        Client newClient = createTestClient(null, "1720456325", true);
        when(clientRepository.existsByIdentification("1720456325")).thenReturn(true);

        // When & Then
        assertThrows(ClientAlreadyExistsException.class, () -> {
            clientService.createClient(newClient);
        });

        verify(clientRepository).existsByIdentification("1720456325");
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe encontrar cliente por ID")
    void shouldFindClientById() {
        // Given
        Client client = createTestClient(1L, "1720456325", true);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        // When
        Optional<Client> result = clientService.findById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("1720456325", result.get().getIdentification());
    }

    @Test
    @DisplayName("Debe retornar vacio cuando cliente no existe")
    void shouldReturnEmptyWhenClientNotFound() {
        // Given
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<Client> result = clientService.findById(999L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Debe encontrar cliente por identificacion")
    void shouldFindClientByIdentification() {
        // Given
        Client client = createTestClient(1L, "1720456325", true);
        when(clientRepository.findByIdentification("1720456325")).thenReturn(Optional.of(client));

        // When
        Optional<Client> result = clientService.findByIdentification("1720456325");

        // Then
        assertTrue(result.isPresent());
        assertEquals("1720456325", result.get().getIdentification());
    }

    @Test
    @DisplayName("Debe listar todos los clientes")
    void shouldFindAllClients() {
        // Given
        Client client1 = createTestClient(1L, "1720456325", true);
        Client client2 = createTestClient(2L, "0926548751", true);
        when(clientRepository.findAll()).thenReturn(Arrays.asList(client1, client2));

        // When
        List<Client> result = clientService.findAll();

        // Then
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Debe listar solo clientes activos")
    void shouldFindAllActiveClients() {
        // Given
        Client activeClient = createTestClient(1L, "1720456325", true);
        when(clientRepository.findByActiveTrue()).thenReturn(Arrays.asList(activeClient));

        // When
        List<Client> result = clientService.findAllActive();

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).isActive());
    }

    @Test
    @DisplayName("Debe actualizar cliente existente")
    void shouldUpdateExistingClient() {
        // Given
        Client existingClient = createTestClient(1L, "1720456325", true);
        when(clientRepository.existsById(1L)).thenReturn(true);
        when(clientRepository.save(existingClient)).thenReturn(existingClient);

        // When
        Client result = clientService.updateClient(existingClient);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(clientRepository).existsById(1L);
        verify(clientRepository).save(existingClient);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al actualizar cliente sin ID")
    void shouldThrowExceptionWhenUpdatingClientWithoutId() {
        // Given
        Client clientWithoutId = createTestClient(null, "1720456325", true);

        // When & Then
        assertThrows(ClientNotFoundException.class, () -> {
            clientService.updateClient(clientWithoutId);
        });
    }

    @Test
    @DisplayName("Debe lanzar excepcion al actualizar cliente inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentClient() {
        // Given
        Client nonExistentClient = createTestClient(999L, "1720456325", true);
        when(clientRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(ClientNotFoundException.class, () -> {
            clientService.updateClient(nonExistentClient);
        });
    }

    @Test
    @DisplayName("Debe eliminar cliente inactivo")
    void shouldDeleteInactiveClient() {
        // Given
        Client inactiveClient = createTestClient(1L, "1720456325", false);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(inactiveClient));
        doNothing().when(clientRepository).deleteById(1L);

        // When
        clientService.deleteClient(1L);

        // Then
        verify(clientRepository).findById(1L);
        verify(clientRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepcion al eliminar cliente activo")
    void shouldThrowExceptionWhenDeletingActiveClient() {
        // Given
        Client activeClient = createTestClient(1L, "1720456325", true);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(activeClient));

        // When & Then
        assertThrows(InvalidClientStateException.class, () -> {
            clientService.deleteClient(1L);
        });

        verify(clientRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe lanzar excepcion al eliminar cliente inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentClient() {
        // Given
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ClientNotFoundException.class, () -> {
            clientService.deleteClient(999L);
        });
    }

    @Test
    @DisplayName("Debe activar cliente inactivo")
    void shouldActivateInactiveClient() {
        // Given
        Client inactiveClient = createTestClient(1L, "1720456325", false);
        Client activatedClient = createTestClient(1L, "1720456325", true);
        
        when(clientRepository.findById(1L)).thenReturn(Optional.of(inactiveClient));
        when(clientRepository.save(any(Client.class))).thenReturn(activatedClient);

        // When
        Client result = clientService.activateClient(1L);

        // Then
        assertTrue(result.isActive());
        verify(clientRepository).findById(1L);
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("Debe desactivar cliente activo")
    void shouldDeactivateActiveClient() {
        // Given
        Client activeClient = createTestClient(1L, "1720456325", true);
        Client deactivatedClient = createTestClient(1L, "1720456325", false);
        
        when(clientRepository.findById(1L)).thenReturn(Optional.of(activeClient));
        when(clientRepository.save(any(Client.class))).thenReturn(deactivatedClient);

        // When
        Client result = clientService.deactivateClient(1L);

        // Then
        assertFalse(result.isActive());
        verify(clientRepository).findById(1L);
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion al activar cliente ya activo")
    void shouldThrowExceptionWhenActivatingAlreadyActiveClient() {
        // Given
        Client alreadyActiveClient = createTestClient(1L, "1720456325", true);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(alreadyActiveClient));

        // When & Then
        InvalidClientStateException exception = assertThrows(InvalidClientStateException.class, () -> {
            clientService.activateClient(1L);
        });

        assertEquals("Client is already active", exception.getMessage());
        verify(clientRepository).findById(1L);
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepcion al desactivar cliente ya inactivo")
    void shouldThrowExceptionWhenDeactivatingAlreadyInactiveClient() {
        // Given
        Client alreadyInactiveClient = createTestClient(1L, "1720456325", false);
        when(clientRepository.findById(1L)).thenReturn(Optional.of(alreadyInactiveClient));

        // When & Then
        InvalidClientStateException exception = assertThrows(InvalidClientStateException.class, () -> {
            clientService.deactivateClient(1L);
        });

        assertEquals("Client is already inactive", exception.getMessage());
        verify(clientRepository).findById(1L);
        verify(clientRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepcion al activar cliente inexistente")
    void shouldThrowExceptionWhenActivatingNonExistentClient() {
        // Given
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ClientNotFoundException.class, () -> {
            clientService.activateClient(999L);
        });
    }

    @Test
    @DisplayName("Debe lanzar excepcion al desactivar cliente inexistente")
    void shouldThrowExceptionWhenDeactivatingNonExistentClient() {
        // Given
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ClientNotFoundException.class, () -> {
            clientService.deactivateClient(999L);
        });
    }

    @Test
    @DisplayName("Debe verificar existencia por identificacion")
    void shouldCheckExistsByIdentification() {
        // Given
        when(clientRepository.existsByIdentification("1720456325")).thenReturn(true);

        // When
        boolean exists = clientService.existsByIdentification("1720456325");

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Debe verificar no existencia por identificacion")
    void shouldCheckNotExistsByIdentification() {
        // Given
        when(clientRepository.existsByIdentification("9999999999")).thenReturn(false);

        // When
        boolean exists = clientService.existsByIdentification("9999999999");

        // Then
        assertFalse(exists);
    }
}
