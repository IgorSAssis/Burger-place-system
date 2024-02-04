package br.com.senior.burger_place.domain.product.dto;

import br.com.senior.burger_place.domain.product.Product;
import br.com.senior.burger_place.domain.product.ProductCategory;

import java.util.UUID;

public record ProductDTO(
        UUID id,
        String name,
        Double price,
        String ingredients,
        ProductCategory category,
        String url
) {
    public ProductDTO(Product product) {
        this(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getIngredients(),
                product.getCategory(),
                product.getUrl()
        );
    }
}
