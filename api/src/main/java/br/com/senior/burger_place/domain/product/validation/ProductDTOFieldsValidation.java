package br.com.senior.burger_place.domain.product.validation;

import br.com.senior.burger_place.domain.product.ProductCategory;

public class ProductDTOFieldsValidation {

    public static void validate(String name, String ingredients, Double price, ProductCategory category) {
        if (name == null || name.trim().isBlank()) {
            throw new IllegalArgumentException("nome inválido");
        }

        if (ingredients == null || ingredients.trim().isBlank()) {
            throw new IllegalArgumentException("ingredientes inválidos");
        }

        if (category == null) {
            throw new IllegalArgumentException("categoria inválida");
        }

        if (price == null || price <= 0) {
            throw new IllegalArgumentException("preço inválido");
        }
    }

}
