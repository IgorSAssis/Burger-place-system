package br.com.senior.burger_place.domain.orderItem;

import br.com.senior.burger_place.domain.occupation.Occupation;
import br.com.senior.burger_place.domain.occupation.dto.UpdateOrderItemDTO;
import br.com.senior.burger_place.domain.product.Product;
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
    private int amount;
    private double itemValue;
    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;
    private String observation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occupation_id")
    private Occupation occupation;
    private boolean active;

    public OrderItem(int amount, double itemValue, Product product) {
        this.amount = amount;
        this.itemValue = itemValue;
        this.product = product;
        this.active = true;
    }

    public OrderItem(
            int amount,
            double itemValue,
            Product product,
            Occupation occupation,
            String observation
    ) {
        this(amount, itemValue, product);
        this.occupation = occupation;
        this.observation = observation;
        this.status = OrderItemStatus.RECEBIDO;
    }

    public void inactivate() {
        this.active = false;
    }

    public void update(UpdateOrderItemDTO itemDTO) {
        if (itemDTO.amount() != null && itemDTO.amount() > 0) {
            this.amount = itemDTO.amount();
        }

        if (itemDTO.observation() != null) {
            this.observation = itemDTO.observation().isEmpty()
                    ? null
                    : itemDTO.observation();
        }
    }

    public void startPreparation() {
        this.status = OrderItemStatus.EM_ANDAMENTO;
    }
    public void finishPreparation() {
        this.status = OrderItemStatus.PRONTO;
    }
    public void cancel() {
        this.status = OrderItemStatus.CANCELADO;
    }
    public void deliver() {
        this.status = OrderItemStatus.ENTREGUE;
    }
}
