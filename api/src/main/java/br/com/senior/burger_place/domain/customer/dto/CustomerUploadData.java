package br.com.senior.burger_place.domain.customer.dto;

import br.com.senior.burger_place.domain.address.AdressData;
import jakarta.validation.Valid;

public record CustomerUploadData(
        String name,
        String email,
        @Valid
        AdressData adressData) {
}
