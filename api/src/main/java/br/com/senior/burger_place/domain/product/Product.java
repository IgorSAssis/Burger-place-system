package br.com.senior.burger_place.domain.product;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "products")
@Entity(name = "Product")
@EqualsAndHashCode
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private Double price;
    private String ingredients;
    @Enumerated(EnumType.STRING)
    private ProductCategory category;
    private String url;
    @Builder.Default
    private Boolean active = true;

    public void update(
            String newName,
            String newIngredients,
            Double newPrice,
            ProductCategory newCategory,
            String newUrl
    ) {
        if (newName != null) {
            this.name = newName;
        }

        if (newIngredients != null) {
            this.ingredients = newIngredients;
        }

        if (newPrice != null && newPrice > 0) {
            this.price = newPrice;
        }

        if (newCategory != null) {
            this.category = newCategory;
        }

        if (newUrl != null) {
            this.url = newUrl;
        }
    }

    public void inactivate() {
        if (!this.active) {
            throw new IllegalStateException("Product already inactive");
        }

        this.active = false;
    }
}
