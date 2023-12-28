package br.com.senior.burger_place.domain.product.validation;

public class ProductDTOFieldsValidation {

    public static void validate(String name, Double price) {
        if (name == null || name.trim().isBlank()) {
            throw new IllegalArgumentException("nome inválido");
        }

        if (price == null || price <= 0) {
            throw new IllegalArgumentException("preço inválido");
        }
    }

}
