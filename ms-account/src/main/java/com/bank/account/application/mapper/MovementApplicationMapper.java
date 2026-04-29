package com.bank.account.application.mapper;

import com.bank.account.application.dto.MovementRequestDto;
import com.bank.account.application.dto.MovementResponseDto;
import com.bank.account.domain.entity.Movement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

/**
 * MapStruct mapper for Movement DTOs.
 * Maps between domain entities and application DTOs.
 */
@Mapper(componentModel = "spring")
public interface MovementApplicationMapper {

    /**
     * Maps a MovementRequestDto to a domain Movement entity.
     * Uses factory methods based on type.
     *
     * @param requestDto the request DTO
     * @return the domain entity
     */
    default Movement toDomain(MovementRequestDto requestDto) {
        if (requestDto == null) {
            return null;
        }

        // Calculate balance after movement (initially same as amount)
        // In real flow, this should be calculated based on current account balance
        BigDecimal balanceAfter = requestDto.getAmount();

        // Use factory method based on type
        if ("Deposito".equalsIgnoreCase(requestDto.getType())) {
            return Movement.createDeposit(
                    requestDto.getAccountNumber(),
                    requestDto.getAmount(),
                    balanceAfter
            );
        } else if ("Retiro".equalsIgnoreCase(requestDto.getType())) {
            return Movement.createWithdrawal(
                    requestDto.getAccountNumber(),
                    requestDto.getAmount(),
                    balanceAfter
            );
        } else {
            // Fallback to constructor (will validate type)
            return new Movement(
                    requestDto.getAccountNumber(),
                    requestDto.getDate(),
                    requestDto.getType(),
                    requestDto.getAmount(),
                    balanceAfter
            );
        }
    }

    /**
     * Maps a domain Movement entity to a MovementResponseDto.
     *
     * @param movement the domain entity
     * @return the response DTO
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "accountNumber", target = "accountNumber")
    @Mapping(source = "date", target = "date")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "balance", target = "balance")
    MovementResponseDto toResponseDto(Movement movement);

    /**
     * Creates a Movement from transaction data.
     * Used when recording movements from Account transactions.
     *
     * @param accountNumber the account number
     * @param type the movement type (Deposito/Retiro)
     * @param amount the amount
     * @param balanceAfter the balance after the movement
     * @return the domain entity
     */
    default Movement fromTransaction(String accountNumber, String type, BigDecimal amount, BigDecimal balanceAfter) {
        if ("Deposito".equalsIgnoreCase(type)) {
            return Movement.createDeposit(accountNumber, amount, balanceAfter);
        } else if ("Retiro".equalsIgnoreCase(type)) {
            return Movement.createWithdrawal(accountNumber, amount, balanceAfter);
        } else {
            return new Movement(accountNumber, null, type, amount, balanceAfter);
        }
    }
}
