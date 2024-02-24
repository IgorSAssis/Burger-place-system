package br.com.senior.burger_place.domain.product.dto;

import br.com.senior.burger_place.domain.product.ProductCategory;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
public class ProductDTO {
    private UUID id;
    private String name;
    private Double price;
    private String ingredients;
    private ProductCategory category;
    private String url;
    private Boolean active;
}
