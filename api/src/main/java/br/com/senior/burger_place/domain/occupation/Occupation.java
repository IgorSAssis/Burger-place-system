package br.com.senior.burger_place.domain.occupation;

import br.com.senior.burger_place.domain.board.Board;
import br.com.senior.burger_place.domain.customer.Customer;
import br.com.senior.burger_place.domain.orderItem.OrderItem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "occupations")
@Entity(name = "Occupation")
@ToString
public class Occupation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private LocalDateTime beginOccupation;
    private LocalDateTime endOccupation;
    private Integer peopleCount;
    @Enumerated(EnumType.STRING)
    private PaymentForm paymentForm;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "occupation")
    @Builder.Default
    private List<OrderItem> orderItems = List.of();
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
    @ManyToMany
    @JoinTable(
            name = "customer_occupations",
            joinColumns = @JoinColumn(name = "occupation_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    @Builder.Default
    private Set<Customer> customers = Set.of();
    @Builder.Default
    private boolean active = true;

    public void inactivate() {
        this.active = false;
    }

    public void finish(LocalDateTime endOccupation, PaymentForm paymentForm) {
        this.endOccupation = endOccupation;
        this.paymentForm = paymentForm;
    }
}
