package br.com.senior.burger_place.domain.product.dto;

import br.com.senior.burger_place.domain.product.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateProductDTO(
        @NotBlank
        String name,
        @NotBlank
        String ingredients,
        @NotNull
        @Positive
        Double price,
        @NotNull
        ProductCategory category,
        String url
) {
}
