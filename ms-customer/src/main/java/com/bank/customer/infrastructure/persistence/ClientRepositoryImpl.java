package com.bank.customer.infrastructure.persistence;

import com.bank.customer.domain.entity.Client;
import com.bank.customer.domain.repository.ClientRepository;
import com.bank.customer.infrastructure.persistence.entity.ClientJpaEntity;
import com.bank.customer.infrastructure.persistence.mapper.ClientMapper;
import com.bank.customer.infrastructure.persistence.repository.ClientJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ClientRepositoryImpl implements ClientRepository {

    private final ClientJpaRepository jpaRepository;
    private final ClientMapper mapper;

    public ClientRepositoryImpl(ClientJpaRepository jpaRepository, ClientMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Client save(Client client) {
        ClientJpaEntity jpaEntity = mapper.toJpa(client);
        ClientJpaEntity saved = jpaRepository.save(jpaEntity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Client> findById(Long id) {
        return jpaRepository.findById(id)
            .map(mapper::toDomain);
    }
    

    @Override
    public Optional<Client> findByIdentification(String identification) {
        return jpaRepository.findByIdentification(identification)
            .map(mapper::toDomain);
    }

    @Override
    public List<Client> findAll() {
        return jpaRepository.findAll().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<Client> findByActiveTrue() {
        return jpaRepository.findByActiveTrue().stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(Client client) {
        ClientJpaEntity jpaEntity = mapper.toJpa(client);
        jpaRepository.delete(jpaEntity);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByIdentification(String identification) {
        return jpaRepository.existsByIdentification(identification);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}