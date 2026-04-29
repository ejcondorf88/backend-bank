package com.bank.customer.domain.port.in;

import com.bank.customer.domain.entity.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {

    Client createClient(Client client);

    Optional<Client> findById(Long id);

    Client getById(Long id);

    Optional<Client> findByIdentification(String identification);

    Client getByIdentification(String identification);

    List<Client> findAll();

    List<Client> findAllActive();

    Client updateClient(Client client);

    void deleteClient(Long clientId);

    Client activateClient(Long clientId);

    Client deactivateClient(Long clientId);

    boolean existsByIdentification(String identification);
}