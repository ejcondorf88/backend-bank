package com.bank.account.infrastructure.persistence;

import com.bank.account.domain.entity.Movement;
import com.bank.account.domain.repository.MovementRepository;
import com.bank.account.infrastructure.persistence.entity.MovementJpaEntity;
import com.bank.account.infrastructure.persistence.mapper.MovementJpaMapper;
import com.bank.account.infrastructure.persistence.repository.MovementJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the domain MovementRepository interface.
 * Bridges the domain layer with the infrastructure JPA repository.
 */
@Repository
public class MovementRepositoryImpl implements MovementRepository {

    private final MovementJpaRepository jpaRepository;
    private final MovementJpaMapper jpaMapper;

    public MovementRepositoryImpl(MovementJpaRepository jpaRepository, MovementJpaMapper jpaMapper) {
        this.jpaRepository = jpaRepository;
        this.jpaMapper = jpaMapper;
    }

    @Override
    public Movement save(Movement movement) {
        MovementJpaEntity jpaEntity = jpaMapper.toJpaEntity(movement);
        MovementJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return jpaMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Movement> findById(Long id) {
        return jpaRepository.findById(id)
                .map(jpaMapper::toDomain);
    }

    @Override
    public List<Movement> findAll() {
        return jpaRepository.findAll().stream()
                .map(jpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movement> findByAccountNumber(String accountNumber) {
        return jpaRepository.findByAccountNumber(accountNumber).stream()
                .map(jpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movement> findByAccountNumberAndDateBetween(String accountNumber,
                                                            LocalDateTime startDate,
                                                            LocalDateTime endDate) {
        return jpaRepository.findByAccountNumberAndDateBetween(accountNumber, startDate, endDate).stream()
                .map(jpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movement> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.findByDateBetween(startDate, endDate).stream()
                .map(jpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Movement> findByClientId(Long clientId) {
        return jpaRepository.findByClientId(clientId).stream()
                .map(jpaMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    public boolean existsByAccountNumber(String accountNumber) {
        return jpaRepository.countByAccountNumber(accountNumber) > 0;
    }

    @Override
    public long countByAccountNumber(String accountNumber) {
        return jpaRepository.countByAccountNumber(accountNumber);
    }
}
