package br.com.senior.burger_place.domain.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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

//    @Autowired
//    CustomerRepository customerRepository;
//
//    public Customer addCustomer(CustomerRegistrationDTO data) {
//        if (customerRepository.existsByCpf(data.cpf())) {
//            throw new DuplicateKeyException("Já existe um cliente com o mesmo cpf.");
//        }
//        if (customerRepository.existsByEmail(data.email())) {
//            throw new DuplicateKeyException("Já existe um cliente com o mesmo e-mail.");
//        }
//        if (data.cpf().length() > 11) {
//            throw new IllegalArgumentException("O CPF deve ter 11 dígitos");
//
//        }
//        return customerRepository.save(new Customer(data));
//    }
//
//    public Page<ListingCustomersDTO> listCustomer(Pageable pageable) {
//        return customerRepository.findAllByActiveTrue(pageable).map(ListingCustomersDTO::new);
//    }
//
//    public Customer listCustomerById(Long id) {
//        Customer customer = customerRepository.getReferenceByIdAndActiveTrue(id);
//        if (customer == null) {
//            throw new EntityNotFoundException("Cliente não existe ou está inativado");
//        }
//        return customer;
//    }
//
//    public void updateCustomer(Long id, CustomerUpdatedDTO data) {
//        Customer customer = customerRepository.getReferenceByIdAndActiveTrue(id);
//        if (customer == null) {
//            throw new EntityNotFoundException("Cliente não existe ou está inativado");
//        }
//        customer.updateInformation(data);
//    }
}