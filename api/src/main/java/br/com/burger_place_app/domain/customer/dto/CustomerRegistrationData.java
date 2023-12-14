package br.com.burger_place_app.domain.customer.dto;

import br.com.burger_place_app.domain.address.AdressData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CustomerRegistrationData(
        @NotBlank
        String name,
        @NotBlank
        String email,
        @NotBlank
        String cpf,
        @NotNull
        @Valid
        AdressData address,

        boolean active

) {
}
