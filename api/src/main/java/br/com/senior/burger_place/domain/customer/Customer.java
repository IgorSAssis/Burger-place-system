package br.com.senior.burger_place.domain.customer;

import br.com.senior.burger_place.domain.address.Address;
import br.com.senior.burger_place.domain.customer.dto.CustomerRegistrationDTO;
import br.com.senior.burger_place.domain.customer.dto.CustomerUpdatedDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "customers")
@Entity(name = "Customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String cpf;
    private boolean active;
    @Embedded
    private Address address;

    public Customer(CustomerRegistrationDTO data) {
        this.active = true;
        this.name = data.name();
        this.email = data.email();
        this.cpf = data.cpf();
        this.address = new Address(data.address());
    }

    public void updateInformation(CustomerUpdatedDTO data) {
        if (data.name() == null && data.email() == null && data.adressDto() == null) {
            throw new IllegalArgumentException("Para poder atualizar um cliente, é necessário informar ao menos um dado válido");
        }
        if (data.name() != null) {
            this.name = data.name();
        }
        if (data.email() != null) {
            this.email = data.email();
        }
        if (data.adressDto() != null) {
            this.address.updateInformationAdress(data.adressDto());
        }
    }

    public void inactivate() {
        this.active = false;
    }
}
