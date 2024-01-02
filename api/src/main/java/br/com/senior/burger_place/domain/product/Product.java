package br.com.senior.burger_place.domain.product;

import br.com.senior.burger_place.domain.product.dto.UpdateProductDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "products")
@Entity(name = "Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private String ingredients;
    @Enumerated(EnumType.STRING)
    private ProductCategory category;
    private String url;
    private boolean active;

    public Product(String name, String ingredients, Double price, ProductCategory category, String url) {
        this.name = name;
        this.ingredients = ingredients;
        this.price = price;
        this.category = category;
        this.url = url;
        this.active = true;
    }

    public void update(UpdateProductDTO productData) {
        if (productData.name() != null) {
            this.name = productData.name();
        }

        if (productData.ingredients() != null) {
            this.ingredients = productData.ingredients();
        }

        if (productData.price() != null && productData.price() > 0) {
            this.price = productData.price();
        }

        if (productData.category() != null) {
            this.category = productData.category();
        }

        if (productData.url() != null) {
            this.url = productData.url();
        }
    }

    public void inactivate() {
        this.active = false;
    }
}
