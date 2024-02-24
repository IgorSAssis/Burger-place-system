package utils;

import br.com.senior.burger_place.domain.product.Product;
import br.com.senior.burger_place.domain.product.ProductCategory;
import br.com.senior.burger_place.domain.product.dto.CreateProductDTO;
import br.com.senior.burger_place.domain.product.dto.ProductDTO;
import br.com.senior.burger_place.domain.product.dto.UpdateProductDTO;

import java.util.UUID;

public class ProductCreator {

    public static Product createProduct() {
        return Product.builder()
                .id(UUID.randomUUID())
                .name("Hamburguer tradicional")
                .price(30.40)
                .ingredients("Pão de brioche, queijo, carne, tomate, cebola caramelizada, alface, pickles")
                .category(ProductCategory.BURGER)
                .url("someURL")
                .build();
    }

    public static ProductDTO createProductDTO() {
        return ProductDTO.builder()
                .id(UUID.randomUUID())
                .name("Hamburguer tradicional")
                .price(30.40)
                .ingredients("Pão de brioche, queijo, carne, tomate, cebola caramelizada, alface, pickles")
                .category(ProductCategory.BURGER)
                .url("someURL")
                .active(true)
                .build();
    }

    public static CreateProductDTO createCreateProductDTO() {
        return CreateProductDTO.builder()
                .name("Hamburguer tradicional")
                .price(30.40)
                .ingredients("Pão de brioche, queijo, carne, tomate, cebola caramelizada, alface, pickles")
                .category(ProductCategory.BURGER)
                .url("someURL")
                .build();
    }

    public static UpdateProductDTO createUpdateProductDTO() {
        return UpdateProductDTO.builder()
                .name("Hamburguer tradicional")
                .price(30.40)
                .ingredients("Pão de brioche, queijo, carne, tomate, cebola caramelizada, alface, pickles")
                .category(ProductCategory.BURGER)
                .url("someURL")
                .build();
    }
}
