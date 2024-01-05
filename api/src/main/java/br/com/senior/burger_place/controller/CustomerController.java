package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.customer.Customer;
import br.com.senior.burger_place.domain.customer.CustomerService;
import br.com.senior.burger_place.domain.customer.dto.CustomerRegistrationDTO;
import br.com.senior.burger_place.domain.customer.dto.CustomerUpdatedDTO;
import br.com.senior.burger_place.domain.customer.dto.ListingCustomersDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    @Transactional
    public ResponseEntity<Object> register(@RequestBody @Valid CustomerRegistrationDTO dto, UriComponentsBuilder uriBuilder) {
        Customer customer = customerService.addCustomer(dto);
        var uri = uriBuilder.path("/customers/{id}").buildAndExpand(customer.getId()).toUri();

        return ResponseEntity.created(uri).body(new ListingCustomersDTO(customer));
    }

    @GetMapping
    public ResponseEntity<Page<ListingCustomersDTO>> listAllCustomer(@PageableDefault(size = 5, sort = {"name"}) Pageable pageable) {
        Page<ListingCustomersDTO> customers = customerService.listCustomer(pageable);
        return ResponseEntity.ok().body(customers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> listCustomerById(@PathVariable Long id) {
        Customer customer = customerService.listCustomerById(id);
        return ResponseEntity.ok(new ListingCustomersDTO(customer));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Object> updateCustomer(
            @PathVariable
            Long id,
            @RequestBody
            @Valid
            CustomerUpdatedDTO dto
    ) {
        customerService.updateCustomer(id, dto);
        Customer customer = customerService.listCustomerById(id);
        ListingCustomersDTO updatedData = new ListingCustomersDTO((customer));
        return ResponseEntity.status(HttpStatus.OK).body(updatedData);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Object> deleteCustomer(@PathVariable Long id) {
        Customer customer = customerService.listCustomerById(id);
        customer.inactivate();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}