package br.com.burger_place_app.domain.order;

import br.com.burger_place_app.domain.customer.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "orders")
@Entity(name = "Order")
@ToString
public class Order {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    @Enumerated(EnumType.STRING)
    private PaymentForm paymentForm;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    private List<OrderItem> orderItems;
    private boolean active;

    public Order(LocalDateTime openedAt, LocalDateTime closedAt, PaymentForm paymentForm, OrderStatus status, Customer customer, List<OrderItem> orderItems) {
        this.openedAt = openedAt;
        this.closedAt = closedAt;
        this.paymentForm = paymentForm;
        this.status = status;
        this.customer = customer;
        this.orderItems = orderItems;
    }

    public void inactivate() {
        this.active = false;
    }
}
