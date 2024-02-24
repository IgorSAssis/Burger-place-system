package br.com.senior.burger_place.domain.occupation.dto;

import br.com.senior.burger_place.domain.customer.Customer;

import java.util.UUID;

public record CustomerOccupationDTO(
        UUID id,
        String name
) {
    public CustomerOccupationDTO(Customer customer) {
        this(
                customer.getId(),
                customer.getName()
        );
    }
}
