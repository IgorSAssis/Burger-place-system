package br.com.senior.burger_place.domain.address;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AdressDto(
        @NotBlank
        @Column(name = "street_address")
        String streetAddress,
        @NotBlank
        String neighborhood,
        @NotBlank
        String city,
        @NotBlank
        String state,
        @NotBlank @Pattern(regexp = "\\d{8}")
        @Column(name = "postal_code")
        String postalCode,
        String residentialNumber,
        String complement
) {
}
