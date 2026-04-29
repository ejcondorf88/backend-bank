package com.bank.account.infrastructure.persistence.mapper;

import com.bank.account.domain.entity.Account;
import com.bank.account.infrastructure.persistence.entity.AccountJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class AccountJpaMapper {

    /**
     * Maps a JPA entity to a domain entity.
     *
     * @param jpaEntity the JPA entity
     * @return the domain entity
     */
    public Account toDomain(AccountJpaEntity jpaEntity) {
        if (jpaEntity == null) {
            return null;
        }

        // Create domain entity using constructor
        Account account = new Account(
                jpaEntity.getAccountNumber(),
                jpaEntity.getAccountType(),
                jpaEntity.getBalance(),
                jpaEntity.isActive(),
                jpaEntity.getClientId()
        );

        // Set the ID
        account.setId(jpaEntity.getId());

        return account;
    }

    /**
     * Maps a domain entity to a JPA entity.
     *
     * @param domain the domain entity
     * @return the JPA entity
     */
    public AccountJpaEntity toJpaEntity(Account domain) {
        if (domain == null) {
            return null;
        }

        AccountJpaEntity jpaEntity = new AccountJpaEntity();
        jpaEntity.setId(domain.getId());
        jpaEntity.setAccountNumber(domain.getAccountNumber());
        jpaEntity.setAccountType(domain.getAccountType());
        jpaEntity.setBalance(domain.getBalance());
        jpaEntity.setActive(domain.isActive());
        jpaEntity.setClientId(domain.getClientId());

        return jpaEntity;
    }
}
