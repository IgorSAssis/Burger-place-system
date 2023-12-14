package br.com.burger_place_app.domain.customer;

import br.com.burger_place_app.domain.customer.dto.CustomerRegistrationData;
import br.com.burger_place_app.domain.customer.dto.CustomerUploadData;
import br.com.burger_place_app.domain.address.Address;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "customers")
@Entity(name = "Customer")
public class Customer {
        @Id
        @GeneratedValue(strategy =  GenerationType.IDENTITY)
        private Long id;
        private String name;
        private String email;
        private String cpf;
        private boolean active;
        @Embedded
        private Address address;
        public Customer(CustomerRegistrationData data) {
                this.name = data.name();
                this.email = data.email();
                this.cpf = data.cpf();
                this.address = new Address(data.address());
        }

        public void updateInformation(CustomerUploadData data) {
                if (data.name() != null){
                        this.name = data.name();
                }
                if (data.email() != null){
                        this.email = data.email();
                }
                if (data.adressData() != null){
                        this.address.updateInformation(data.adressData());
                }
        }
}
