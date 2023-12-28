package br.com.senior.burger_place.domain.product.dto;

import br.com.senior.burger_place.domain.product.Product;

public record ProductDTO(
        Long id,
        String name,
        Double price,
        String description
) {
    public ProductDTO(Product product) {
        this(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getDescription()
        );
    }
}
