package com.bank.account.application.mapper;

import com.bank.account.application.dto.AccountRequestDto;
import com.bank.account.application.dto.AccountResponseDto;
import com.bank.account.domain.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for Account DTOs.
 * Maps between domain entities and application DTOs.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface AccountApplicationMapper {

    /**
     * Maps an AccountRequestDto to a domain Account entity.
     * Uses constructor to create new account with validations.
     *
     * @param requestDto the request DTO
     * @return the domain entity
     */
    @Mapping(target = "id", ignore = true)
    default Account toDomain(AccountRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }
        return new Account(
                requestDto.getAccountNumber(),
                requestDto.getAccountType(),
                requestDto.getInitialBalance(),
                requestDto.getActive(),
                requestDto.getClientId()
        );
    }

    /**
     * Maps a domain Account entity to an AccountResponseDto.
     *
     * @param account the domain entity
     * @return the response DTO
     */
    @Mapping(source = "active", target = "active")
    AccountResponseDto toResponseDto(Account account);

    /**
     * Updates an existing Account entity from a request DTO.
     * Creates new instance since domain setters are protected.
     *
     * @param existingAccount the existing entity (for ID reference)
     * @param requestDto      the request DTO with new values
     * @return the updated account
     */
    default Account updateDomainFromDto(Account existingAccount, AccountRequestDto requestDto) {
        if (requestDto == null) {
            return existingAccount;
        }

        // Create new account with constructor (validates all fields)
        Account updatedAccount = new Account(
                requestDto.getAccountNumber(),
                requestDto.getAccountType(),
                requestDto.getInitialBalance(),
                requestDto.getActive(),
                requestDto.getClientId()
        );

        // Preserve the original ID
        updatedAccount.setId(existingAccount.getId());

        return updatedAccount;
    }
}
