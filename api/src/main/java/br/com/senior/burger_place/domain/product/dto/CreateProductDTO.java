package br.com.senior.burger_place.domain.product.dto;

import br.com.senior.burger_place.domain.product.ProductCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateProductDTO {
    @NotBlank(message = "Product name cannot be null or blank")
    private String name;
    @NotBlank(message = "Product ingredients cannot be null or blank")
    private String ingredients;
    @NotNull(message = "Product price cannot be null")
    @Positive(message = "Product price must be higher than zero")
    private Double price;
    @NotNull(message = "Product category cannot be null")
    private ProductCategory category;
    private String url;
}
