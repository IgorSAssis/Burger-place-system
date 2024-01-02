package br.com.senior.burger_place.domain.customer.dto;

import br.com.senior.burger_place.domain.address.AdressDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomerRegistrationDTO(
        @NotBlank
        String name,
        @NotBlank
        String email,
        @NotBlank
        String cpf,
        @NotNull
        @Valid
        AdressDto address

) {
}
