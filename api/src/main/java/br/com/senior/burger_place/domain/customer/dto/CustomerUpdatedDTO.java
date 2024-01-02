package br.com.senior.burger_place.domain.customer.dto;

import br.com.senior.burger_place.domain.address.AdressDto;
import jakarta.validation.Valid;

public record CustomerUpdatedDTO(
        String name,
        String email,
        @Valid
        AdressDto adressDto) {
}
