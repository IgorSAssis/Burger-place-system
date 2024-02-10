package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.customer.CustomerConverter;
import br.com.senior.burger_place.domain.customer.CustomerService;
import br.com.senior.burger_place.domain.customer.dto.CreateCustomerDTO;
import br.com.senior.burger_place.domain.customer.dto.CustomerDTO;
import br.com.senior.burger_place.domain.customer.dto.UpdateCustomerDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerConverter customerConverter;

    @GetMapping
    public ResponseEntity<Page<CustomerDTO>> list(
            Pageable pageable,
            @RequestParam(name = "name", required = false)
            String name,
            @RequestParam(name = "email", required = false)
            String email,
            @RequestParam(name = "active", required = false)
            Boolean active
    ) {
        Page<CustomerDTO> customers = this.customerService.listCustomers(
                pageable,
                name,
                email,
                active
        ).map(this.customerConverter::toCustomerDTO);

        return ResponseEntity.ok(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> show(
            @PathVariable
            UUID id
    ) {
        return ResponseEntity.ok(
                this.customerConverter.toCustomerDTO(this.customerService.showCustomer(id))
        );
    }

    @PostMapping
    public ResponseEntity<CustomerDTO> create(
            @Valid
            @RequestBody
            CreateCustomerDTO dto,
            UriComponentsBuilder uriBuilder
    ) {
        CustomerDTO customerDTO = this.customerConverter.toCustomerDTO(this.customerService.createCustomer(dto));

        URI uri = uriBuilder
                .path("/customers/{id}")
                .buildAndExpand(customerDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(customerDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> update(
            @PathVariable
            UUID id,
            @Valid
            @RequestBody
            UpdateCustomerDTO dto
    ) {
        return ResponseEntity.ok(
                this.customerConverter.toCustomerDTO(this.customerService.updateCustomer(id, dto))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> inactivate(
            @PathVariable
            UUID id
    ) {
        this.customerService.inactivateCustomer(id);

        return ResponseEntity.noContent().build();
    }
}