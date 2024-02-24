package br.com.senior.burger_place.domain.customer;

import br.com.senior.burger_place.domain.customer.dto.CustomerDTO;
import org.springframework.stereotype.Component;

@Component
public class CustomerConverter {

    public CustomerDTO toCustomerDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .cpf(customer.getCpf())
                .active(customer.getActive())
                .build();
    }

}
