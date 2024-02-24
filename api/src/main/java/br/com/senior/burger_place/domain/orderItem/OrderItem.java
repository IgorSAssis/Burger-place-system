package br.com.senior.burger_place.domain.orderItem;

import br.com.senior.burger_place.domain.occupation.Occupation;
import br.com.senior.burger_place.domain.product.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
@Table(name = "order_items")
@Entity(name = "OrderItem")
@SQLRestriction("active = TRUE")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
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
    @Builder.Default
    private boolean active = true;

    public void inactivate() {
        if (!this.active) {
            throw new IllegalStateException("Order item already inactive");
        }

        this.active = false;
    }

    public void update(Integer newAmount, String newObservation) {
        if (newAmount != null) {
            this.amount = newAmount;
        }

        if (newObservation != null) {
            this.observation = newObservation;
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
