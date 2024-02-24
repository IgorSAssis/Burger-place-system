package br.com.senior.burger_place.utils;

import br.com.senior.burger_place.domain.customer.Customer;

import java.util.UUID;

public class CustomerTestFactory {
    public static Customer customerFactory(UUID id) {
        return new Customer(id, "Cliente 01", null, null, true);
    }
}
