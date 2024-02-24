package br.com.senior.burger_place.domain.customer;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class CustomerSpecification {

    private Specification<Customer> filterByCustomerName(String customerName) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + customerName.toLowerCase(Locale.ROOT) + "%"
                );
    }

    private Specification<Customer> filterByCustomerEmail(String customerEmail) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("email")),
                        "%" + customerEmail.toLowerCase(Locale.ROOT) + "%"
                );
    }

    private Specification<Customer> filterByActive(Boolean active) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("active"),
                        active
                );
    }

    public Specification<Customer> applyFilters(
            String customerName,
            String customerEmail,
            Boolean active
    ) {
        Specification<Customer> customerSpecification = Specification.where(null);

        if (customerName != null && !customerName.isBlank()) {
            customerSpecification = customerSpecification.and(this.filterByCustomerName(customerName));
        }

        if (customerEmail != null && !customerEmail.isBlank()) {
            customerSpecification = customerSpecification.and(this.filterByCustomerEmail(customerEmail));
        }

        if (active != null) {
            customerSpecification = customerSpecification.and(filterByActive(active));
        }

        return customerSpecification;
    }

}
