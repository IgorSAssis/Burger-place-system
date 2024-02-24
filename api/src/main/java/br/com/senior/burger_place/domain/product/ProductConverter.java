package br.com.senior.burger_place.domain.product;

import br.com.senior.burger_place.domain.product.dto.ProductDTO;
import org.springframework.stereotype.Component;

@Component
public class ProductConverter {

    public ProductDTO toProductDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .category(product.getCategory())
                .ingredients(product.getIngredients())
                .url(product.getUrl())
                .active(product.getActive())
                .build();
    }
}
