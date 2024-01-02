package br.com.senior.burger_place.domain.product.dto;

import br.com.senior.burger_place.domain.product.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateProductDTO(
        String name,
        String ingredients,
        @Positive
        Double price,
        ProductCategory category,
        String url
) {
}
