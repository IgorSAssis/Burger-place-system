package br.com.burger_place_app.domain.order;

import br.com.burger_place_app.domain.order.dto.UpdateOrderItemData;
import br.com.burger_place_app.domain.product.Product;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
@Table(name = "order_items")
@Entity(name = "OrderItem")
@SQLRestriction("active = TRUE")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int qtdItens;
    private double itemValue;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    private boolean active;

    public OrderItem(int qtdItens, double itemValue, Product product) {
        this.qtdItens = qtdItens;
        this.itemValue = itemValue;
        this.product = product;
        this.active = true;
    }

    public OrderItem(int qtdItens, double itemValue, Product product, Order order) {
        this(qtdItens, itemValue, product);
        this.order = order;
    }

    public void inactivate() {
        this.active = false;
    }

    public void update(UpdateOrderItemData orderItemData) {
        if (orderItemData.amount() != null && orderItemData.amount() > 0) {
            this.qtdItens = orderItemData.amount();
        }
    }
}
