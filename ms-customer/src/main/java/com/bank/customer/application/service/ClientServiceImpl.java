package com.bank.customer.application.service;

import com.bank.customer.domain.entity.Client;
import com.bank.customer.domain.event.ClientEvent;
import com.bank.customer.domain.event.ClientEventPayload;
import com.bank.customer.domain.event.DomainEventPublisher;
import com.bank.customer.domain.exception.ClientAlreadyExistsException;
import com.bank.customer.domain.exception.ClientNotFoundException;
import com.bank.customer.domain.exception.InvalidClientStateException;
import com.bank.customer.domain.repository.ClientRepository;
import com.bank.customer.domain.service.ClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final DomainEventPublisher eventPublisher;

    public ClientServiceImpl(ClientRepository clientRepository, DomainEventPublisher eventPublisher) {
        this.clientRepository = clientRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Client createClient(Client client) {
        if (clientRepository.existsByIdentification(client.getIdentification())) {
            throw new ClientAlreadyExistsException(client.getIdentification());
        }
        Client savedClient = clientRepository.save(client);

        // Publicar evento de dominio
        ClientEventPayload payload = createClientPayload(savedClient);
        eventPublisher.publish(ClientEvent.clientCreated(
                savedClient.getId(),
                savedClient.getIdentification(),
                savedClient.getName(),
                savedClient.isActive(),
                payload
        ));

        return savedClient;
    }

    @Override
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    @Override
    public Client getById(Long id) {
        return clientRepository.findById(id)
            .orElseThrow(() -> new ClientNotFoundException(id));
    }

    @Override
    public Optional<Client> findByIdentification(String identification) {
        return clientRepository.findByIdentification(identification);
    }

    @Override
    public Client getByIdentification(String identification) {
        return clientRepository.findByIdentification(identification)
            .orElseThrow(() -> new ClientNotFoundException(identification));
    }

    @Override
    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    @Override
    public List<Client> findAllActive() {
        return clientRepository.findByActiveTrue();
    }

    @Override
    @Transactional
    public Client updateClient(Client client) {
        if (client.getId() == null || !clientRepository.existsById(client.getId())) {
            throw new ClientNotFoundException(client.getId());
        }
        // Verificar que la identification no pertenece a OTRO cliente distinto
        clientRepository.findByIdentification(client.getIdentification())
                .filter(existing -> !existing.getId().equals(client.getId()))
                .ifPresent(duplicate -> {
                    throw new ClientAlreadyExistsException(client.getIdentification());
                });
        Client updatedClient = clientRepository.save(client);

        // Publicar evento de dominio
        ClientEventPayload payload = createClientPayload(updatedClient);
        eventPublisher.publish(ClientEvent.clientUpdated(
                updatedClient.getId(),
                updatedClient.getIdentification(),
                updatedClient.getName(),
                updatedClient.isActive(),
                payload
        ));

        return updatedClient;
    }

    @Override
    @Transactional
    public void deleteClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        if (client.isActive()) {
            throw new InvalidClientStateException("Cannot delete active client. Deactivate first.");
        }

        String identification = client.getIdentification();
        clientRepository.deleteById(clientId);

        // Publicar evento de dominio
        eventPublisher.publish(ClientEvent.clientDeleted(clientId, identification));
    }

    @Override
    @Transactional
    public Client activateClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        client.activate();
        Client activatedClient = clientRepository.save(client);

        // Publicar evento de dominio
        eventPublisher.publish(ClientEvent.clientActivated(
                activatedClient.getId(),
                activatedClient.getIdentification(),
                activatedClient.getName()
        ));

        return activatedClient;
    }

    @Override
    @Transactional
    public Client deactivateClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        client.deactivate();
        Client deactivatedClient = clientRepository.save(client);

        // Publicar evento de dominio
        eventPublisher.publish(ClientEvent.clientDeactivated(
                deactivatedClient.getId(),
                deactivatedClient.getIdentification(),
                deactivatedClient.getName()
        ));

        return deactivatedClient;
    }

    @Override
    public boolean existsByIdentification(String identification) {
        return clientRepository.existsByIdentification(identification);
    }

    /**
     * Crea un payload con los datos del cliente para los eventos.
     */
    private ClientEventPayload createClientPayload(Client client) {
        return ClientEventPayload.from(
                client.getId(),
                client.getName(),
                client.getIdentification(),
                client.getPhone(),
                client.getAddress(),
                client.isActive()
        );
    }

}
