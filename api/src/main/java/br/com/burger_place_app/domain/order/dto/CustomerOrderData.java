package br.com.burger_place_app.domain.order.dto;

import br.com.burger_place_app.domain.customer.Customer;

public record CustomerOrderData(
        Long id,
        String name
) {
    public CustomerOrderData(Customer customer) {
        this(
                customer.getId(),
                customer.getName()
        );
    }
}
