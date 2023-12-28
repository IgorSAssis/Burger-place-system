package br.com.senior.burger_place.domain.occupation;

import br.com.senior.burger_place.domain.board.Board;
import br.com.senior.burger_place.domain.customer.Customer;
import br.com.senior.burger_place.domain.occupation.dto.FinishOccupationDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "occupations")
@Entity(name = "Occupation")
@ToString
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class Occupation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime beginOccupation;
    private LocalDateTime endOccupation;
    private Integer peopleCount;
    @Enumerated(EnumType.STRING)
    private PaymentForm paymentForm;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "occupation")
    private List<OrderItem> orderItems;
    @ManyToOne(fetch = FetchType.LAZY)
    private Board board;
    @ManyToMany
    @JoinTable(
            name = "customer_occupations",
            joinColumns = @JoinColumn(name = "occupation_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private Set<Customer> customers;
    private boolean active;

    public Occupation(LocalDateTime beginOccupation, Integer peopleCount, Board board) {
        this.beginOccupation = beginOccupation;
        this.peopleCount = peopleCount;
        this.board = board;
        this.active = true;
    }

    public Occupation(Long occupationId) {
        this.id = occupationId;
    }

    public void inactivate() {
        this.active = false;
    }

    public void finish(FinishOccupationDTO occupationDTO) {
        this.endOccupation = occupationDTO.endOccupation();
        this.paymentForm = occupationDTO.paymentForm();
    }
}
