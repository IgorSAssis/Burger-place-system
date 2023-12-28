package br.com.senior.burger_place.utils;

import br.com.senior.burger_place.domain.product.Product;
import br.com.senior.burger_place.domain.product.dto.CreateProductDTO;
import br.com.senior.burger_place.domain.product.dto.UpdateProductDTO;

public class ProductTestFactory {

    public static Product productFactory(Long id) {
        return new Product(
                id,
                "Hamburguer clássico",
                45.8,
                null,
                true
        );
    }

    public static CreateProductDTO createProductDTOFactory(String name, Double price) {
        return new CreateProductDTO(name, price, null);
    }

    public static UpdateProductDTO updateProductDTOFactory(String name, Double price) {
        return new UpdateProductDTO(name, price, null);
    }

}
