package br.com.senior.burger_place.domain.product.validation;

import br.com.senior.burger_place.domain.product.Product;
import jakarta.persistence.EntityNotFoundException;

public class NonExistentProductValidation {
    public static void validate(Product product) {
        if (product == null) {
            throw new EntityNotFoundException("Produto não existe ou foi inativado");
        }
    }
}
