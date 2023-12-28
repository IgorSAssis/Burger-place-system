package br.com.senior.burger_place.domain.product.validation;

import br.com.senior.burger_place.domain.product.dto.CreateProductDTO;

public class ProductDTOValidation {
    public static void validate(Object productDTO) {
        if (productDTO == null) {
            throw new IllegalArgumentException("DTO não pode ser null");
        }
    }
}
