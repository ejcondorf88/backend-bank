package com.bank.customer.application.service;

import com.bank.customer.application.mapper.ClientApplicationMapper;
import com.bank.customer.application.port.in.command.CreateClientCommand;
import com.bank.customer.application.port.in.command.UpdateClientCommand;
import com.bank.customer.domain.entity.Client;
import com.bank.customer.domain.event.ClientEvent;
import com.bank.customer.domain.exception.ClientAlreadyExistsException;
import com.bank.customer.domain.exception.ClientNotFoundException;
import com.bank.customer.domain.exception.InvalidClientStateException;
import com.bank.customer.domain.port.in.ClientService;
import com.bank.customer.domain.port.out.ClientRepository;
import com.bank.customer.domain.port.out.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final DomainEventPublisher eventPublisher;
    private final ClientApplicationMapper mapper;

    public ClientServiceImpl(ClientRepository clientRepository,
                              DomainEventPublisher eventPublisher,
                              ClientApplicationMapper mapper) {
        this.clientRepository = clientRepository;
        this.eventPublisher = eventPublisher;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Client createClient(CreateClientCommand command) {
        Client client = mapper.toDomain(command);

        if (clientRepository.existsByIdentification(client.getIdentification())) {
            throw new ClientAlreadyExistsException(client.getIdentification());
        }
        Client savedClient = clientRepository.save(client);

        // Publicar evento de dominio
        eventPublisher.publish(ClientEvent.fromCreated(savedClient));

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
    public Client updateClient(UpdateClientCommand command) {
        if (command.id() == null || !clientRepository.existsById(command.id())) {
            throw new ClientNotFoundException(command.id());
        }
        Client client = mapper.toDomain(command);

        // Verificar que la identification no pertenece a OTRO cliente distinto
        clientRepository.findByIdentification(client.getIdentification())
                .filter(existing -> !existing.getId().equals(client.getId()))
                .ifPresent(duplicate -> {
                    throw new ClientAlreadyExistsException(client.getIdentification());
                });
        Client updatedClient = clientRepository.save(client);

        // Publicar evento de dominio
        eventPublisher.publish(ClientEvent.fromUpdated(updatedClient));

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

        clientRepository.deleteById(clientId);

        // Publicar evento de dominio
        eventPublisher.publish(ClientEvent.fromDeleted(client));
    }

    @Override
    @Transactional
    public Client activateClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        client.activate();
        Client activatedClient = clientRepository.save(client);

        // Publicar evento de dominio
        eventPublisher.publish(ClientEvent.fromActivated(activatedClient));

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
        eventPublisher.publish(ClientEvent.fromDeactivated(deactivatedClient));

        return deactivatedClient;
    }

    @Override
    public boolean existsByIdentification(String identification) {
        return clientRepository.existsByIdentification(identification);
    }

    }