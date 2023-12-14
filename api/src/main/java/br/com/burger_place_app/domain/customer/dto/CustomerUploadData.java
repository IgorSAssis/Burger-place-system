package br.com.burger_place_app.domain.customer.dto;

import br.com.burger_place_app.domain.address.AdressData;
import jakarta.validation.Valid;

public record CustomerUploadData(
        String name,
        String email,
        @Valid
        AdressData adressData) {
}
