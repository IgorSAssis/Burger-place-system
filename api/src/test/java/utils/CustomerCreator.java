package utils;

import br.com.senior.burger_place.domain.customer.Customer;
import br.com.senior.burger_place.domain.customer.dto.CreateCustomerDTO;
import br.com.senior.burger_place.domain.customer.dto.CustomerDTO;
import br.com.senior.burger_place.domain.customer.dto.UpdateCustomerDTO;

import java.util.UUID;

public class CustomerCreator {
    public static Customer createCustomer() {
        return Customer.builder()
                .id(UUID.randomUUID())
                .name("Roberto Carlos")
                .cpf("12345678900")
                .email("roberto.carlos@gmail.com")
                .build();
    }

    public static CustomerDTO createCustomerDTO() {
        return CustomerDTO.builder()
                .id(UUID.randomUUID())
                .name("Roberto Carlos")
                .cpf("12345678900")
                .email("roberto.carlos@gmail.com")
                .active(true)
                .build();
    }

    public static CreateCustomerDTO createCreateCustomerDTO() {
        return CreateCustomerDTO.builder()
                .name("Roberto Carlos")
                .cpf("12345678900")
                .email("roberto.carlos@gmail.com")
                .build();
    }

    public static UpdateCustomerDTO createUpdateCustomerDTO() {
        return UpdateCustomerDTO.builder()
                .name("Roberto Carlos")
                .email("roberto.carlos@gmail.com")
                .build();
    }
}
