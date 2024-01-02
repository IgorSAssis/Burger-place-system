package br.com.senior.burger_place.domain.customer.dto;

import br.com.senior.burger_place.domain.address.Address;
import br.com.senior.burger_place.domain.customer.Customer;

public record ListingCustomersDTO(
        String name,
        String email,
        Address address

) {
    public ListingCustomersDTO(Customer customer) {
        this(customer.getName(), customer.getEmail(), customer.getAddress());
    }

}
