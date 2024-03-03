package br.com.senior.burger_place.domain.customer;

import br.com.senior.burger_place.domain.customer.dto.CreateCustomerDTO;
import br.com.senior.burger_place.domain.customer.dto.UpdateCustomerDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerSpecification customerSpecification;

    public Page<Customer> listCustomers(
            Pageable pageable,
            String customerName,
            String customerEmail,
            Boolean active
    ) {
        Specification<Customer> specification = this.customerSpecification.applyFilters(
                customerName, customerEmail, active
        );

        return this.customerRepository.findAll(specification, pageable);
    }

    public Customer showCustomer(
            @NonNull
            UUID customerId
    ) {
        return this.customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer does not exist"));
    }

    @Transactional
    public Customer createCustomer(
            @NonNull
            @Valid
            CreateCustomerDTO dto
    ) {
        if (customerRepository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("CPF already registered");
        }
        if (customerRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        Customer customer = Customer.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .cpf(dto.getCpf())
                .build();

        return this.customerRepository.save(customer);
    }

    @Transactional
    public Customer updateCustomer(
            @NonNull
            UUID customerId,
            @NonNull
            @Valid
            UpdateCustomerDTO dto
    ) {
        Customer customer = this.findActiveCustomerById(customerId);

        if (this.customerRepository.existsByActiveTrueAndEmailEqualsAndIdNot(dto.getEmail(), customerId)) {
            throw new IllegalArgumentException("Email already in use");
        }

        customer.update(dto.getName(), dto.getEmail());

        return customer;
    }

    @Transactional
    public void inactivateCustomer(
            @NonNull
            UUID customerId
    ) {
        Customer customer = this.findActiveCustomerById(customerId);

        customer.inactivate();
    }

    public Set<Customer> findCustomersById(
            @NonNull
            Set<UUID> customerIds
    ) {
        Set<Customer> customers = this.customerRepository.findByIdInAndActiveTrue(customerIds);

        if (customers.isEmpty()) {
            throw new EntityNotFoundException("Customers do not exist or are inactive");
        }

        if (customerIds.size() != customers.size()) {
            throw new IllegalArgumentException("Some customers do not exist or are inactive");
        }

        return customers;
    }

    private Customer findActiveCustomerById(UUID customerId) {
        return this.customerRepository.findByIdAndActiveTrue(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer does not exists or is inactive"));
    }
}