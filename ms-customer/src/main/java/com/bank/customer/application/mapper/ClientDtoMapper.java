package com.bank.customer.application.mapper;

import com.bank.customer.application.dto.ClientRequestDto;
import com.bank.customer.application.dto.ClientResponseDto;
import com.bank.customer.domain.entity.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientDtoMapper {

    public Client toDomain(ClientRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return new Client(
            dto.getName(),
            dto.getGender(),
            dto.getAge(),
            dto.getIdentification(),
            dto.getAddress(),
            dto.getPhone(),
            dto.getPassword(),
            dto.getActive() != null ? dto.getActive() : true
        );
    }

    public ClientResponseDto toResponseDto(Client client) {
        if (client == null) {
            return null;
        }

        return new ClientResponseDto(
            client.getId(),
            client.getClientId(),
            client.getName(),
            client.getGender(),
            client.getAge(),
            client.getIdentification(),
            client.getAddress(),
            client.getPhone(),
            client.isActive()
        );
    }
}