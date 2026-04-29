package com.bank.customer.domain.port.out;

import com.bank.customer.domain.entity.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository {

    Client save(Client client);

    Optional<Client> findById(Long id);

    Optional<Client> findByIdentification(String identification);

    List<Client> findAll();

    List<Client> findByActiveTrue();

    void delete(Client client);

    void deleteById(Long id);

    boolean existsByIdentification(String identification);

    boolean existsById(Long id);

    long count();
}