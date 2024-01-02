package br.com.senior.burger_place.utils;

import br.com.senior.burger_place.domain.product.Product;
import br.com.senior.burger_place.domain.product.ProductCategory;
import br.com.senior.burger_place.domain.product.dto.CreateProductDTO;
import br.com.senior.burger_place.domain.product.dto.ProductDTO;
import br.com.senior.burger_place.domain.product.dto.UpdateProductDTO;

public class ProductTestFactory {

    public static Product productFactory(Long id) {
        return new Product(
                id,
                "Hamburguer clássico",
                45.8,
                "Carne, tomate, pepino, bacon, alface, maionese",
                ProductCategory.BURGER,
                "http://some-image.com.br",
                true
        );
    }

    public static CreateProductDTO createProductDTOFactory(
            String name,
            String ingredients,
            ProductCategory categoryDouble,
            Double price
    ) {
        return new CreateProductDTO(name, ingredients, price, categoryDouble, null);
    }

    public static UpdateProductDTO updateProductDTOFactory(
            String name,
            String ingredients,
            Double price,
            ProductCategory category
    ) {
        return new UpdateProductDTO(
                name,
                ingredients,
                price,
                category,
                "http://some-image.com.br"
        );
    }

    public static UpdateProductDTO updateProductDTOFactory(
            String name,
            String ingredients,
            Double price,
            ProductCategory category,
            String url
    ) {
        return new UpdateProductDTO(
                name,
                ingredients,
                price,
                category,
                url
        );
    }

    public static UpdateProductDTO updateProductDTOFactory(String name, Double price) {
        return updateProductDTOFactory(
                name,
                "Carne, tomate, pepino, bacon, alface, maionese",
                price,
                null
        );
    }

    public static ProductDTO productDTOFactory(Long id) {
        return ProductTestFactory.productDTOFactory(
                id,
                "Hamburguer tradicional",
                25.9,
                "Carne, tomate, pepino, bacon, alface, maionese",
                ProductCategory.BURGER
        );
    }

    public static ProductDTO productDTOFactory(
            Long id,
            String name,
            Double price,
            String ingredients,
            ProductCategory category
    ) {
        return new ProductDTO(
                id,
                name,
                price,
                ingredients,
                category,
                null
        );
    }

}
