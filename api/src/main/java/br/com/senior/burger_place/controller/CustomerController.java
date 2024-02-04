package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.customer.CustomerConverter;
import br.com.senior.burger_place.domain.customer.CustomerService;
import br.com.senior.burger_place.domain.customer.dto.CustomerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
//
//    @PostMapping
//    @Transactional
//    public ResponseEntity<Object> register(@RequestBody @Valid CustomerRegistrationDTO dto, UriComponentsBuilder uriBuilder) {
//        Customer customer = customerService.addCustomer(dto);
//        var uri = uriBuilder.path("/customers/{id}").buildAndExpand(customer.getId()).toUri();
//
//        return ResponseEntity.created(uri).body(new ListingCustomersDTO(customer));
//    }
//
//    @GetMapping
//    public ResponseEntity<Page<ListingCustomersDTO>> listAllCustomer(@PageableDefault(size = 5, sort = {"name"}) Pageable pageable) {
//        Page<ListingCustomersDTO> customers = customerService.listCustomer(pageable);
//        return ResponseEntity.ok().body(customers);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Object> listCustomerById(@PathVariable Long id) {
//        Customer customer = customerService.listCustomerById(id);
//        return ResponseEntity.ok(new ListingCustomersDTO(customer));
//    }
//
//    @PutMapping("/{id}")
//    @Transactional
//    public ResponseEntity<Object> updateCustomer(
//            @PathVariable
//            Long id,
//            @RequestBody
//            @Valid
//            CustomerUpdatedDTO dto
//    ) {
//        customerService.updateCustomer(id, dto);
//        Customer customer = customerService.listCustomerById(id);
//        ListingCustomersDTO updatedData = new ListingCustomersDTO((customer));
//        return ResponseEntity.status(HttpStatus.OK).body(updatedData);
//    }
//
//    @DeleteMapping("/{id}")
//    @Transactional
//    public ResponseEntity<Object> deleteCustomer(@PathVariable Long id) {
//        Customer customer = customerService.listCustomerById(id);
//        customer.inactivate();
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
}