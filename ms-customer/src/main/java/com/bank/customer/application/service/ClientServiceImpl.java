package com.bank.customer.application.service;

import com.bank.customer.domain.entity.Client;
import com.bank.customer.domain.exception.ClientAlreadyExistsException;
import com.bank.customer.domain.exception.ClientNotFoundException;
import com.bank.customer.domain.exception.InvalidClientStateException;
import com.bank.customer.domain.repository.ClientRepository;
import com.bank.customer.domain.service.ClientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de los casos de uso de Cliente.
 * Pertenece a la capa de aplicación.
 * Configurado como readOnly por defecto para operaciones de consulta.
 */
@Service
@Transactional(readOnly = true)
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional
    public Client createClient(Client client) {
        if (clientRepository.existsByIdentification(client.getIdentification())) {
            throw new ClientAlreadyExistsException(client.getIdentification());
        }
        return clientRepository.save(client);
    }

    @Override
    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    @Override
    public Optional<Client> findByIdentification(String identification) {
        return clientRepository.findByIdentification(identification);
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
        return clientRepository.save(client);
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
    }

    @Override
    @Transactional
    public Client activateClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new ClientNotFoundException(clientId));

        client.setActive(true);
        return clientRepository.save(client);
    }

    @Override
    @Transactional
    public Client deactivateClient(Long clientId) {
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new ClientNotFoundException(clientId));

        client.setActive(false);
        return clientRepository.save(client);
    }

    @Override
    public boolean existsByIdentification(String identification) {
        return clientRepository.existsByIdentification(identification);
    }

}
