package br.com.senior.burger_place.domain.customer;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "customers")
@Entity(name = "Customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String email;
    private String cpf;
    @Builder.Default
    private Boolean active = true;

    public void update(
            String name,
            String email
    ) {
        if (name != null) {
            this.name = name;
        }

        if (email != null) {
            this.email = email;
        }
    }

    public void inactivate() {
        if (!this.active) {
            throw new IllegalStateException("Customer already inactive");
        }

        this.active = false;
    }
}
