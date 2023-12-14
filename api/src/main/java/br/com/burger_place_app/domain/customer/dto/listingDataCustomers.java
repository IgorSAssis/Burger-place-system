package br.com.burger_place_app.domain.customer.dto;

import br.com.burger_place_app.domain.address.Address;
import br.com.burger_place_app.domain.customer.Customer;

public record listingDataCustomers(
        String name,
        String email,
        Address address

) {
    public listingDataCustomers(Customer customer) {
        this(customer.getName(), customer.getEmail(), customer.getAddress());
    }

}
