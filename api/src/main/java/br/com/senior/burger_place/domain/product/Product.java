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
    private String description;
    private boolean active;

    public Product(String name, double price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.active = true;
    }

    public void update(UpdateProductDTO productData) {
        this.name = productData.name();
        this.price = productData.price();
        this.description = productData.description();
    }

    public void inactivate() {
        this.active = false;
    }
}
