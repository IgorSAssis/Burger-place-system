package br.com.senior.burger_place.utils;

import br.com.senior.burger_place.domain.customer.Customer;

public class CustomerTestFactory {
    public static Customer customerFactory(Long id) {
        return new Customer(1L, "Cliente 01", null, null, true, null);
    }
}
