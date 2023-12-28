package br.com.senior.burger_place.domain.occupation.dto;

import br.com.senior.burger_place.domain.customer.Customer;

public record CustomerOccupationDTO(
        Long id,
        String name
) {
    public CustomerOccupationDTO(Customer customer) {
        this(
                customer.getId(),
                customer.getName()
        );
    }
}
