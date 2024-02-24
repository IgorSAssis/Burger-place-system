package br.com.senior.burger_place.domain.product.dto;

import br.com.senior.burger_place.domain.product.ProductCategory;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateProductDTO {
    private String name;
    private String ingredients;
    @Positive(message = "Product price must be higher than zero")
    private Double price;
    private ProductCategory category;
    private String url;
}
