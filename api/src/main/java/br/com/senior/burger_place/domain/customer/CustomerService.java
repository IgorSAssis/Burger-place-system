package br.com.senior.burger_place.domain.customer;

import br.com.senior.burger_place.domain.customer.dto.CustomerRegistrationData;
import br.com.senior.burger_place.domain.customer.dto.CustomerUploadData;
import br.com.senior.burger_place.domain.customer.dto.listingDataCustomers;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    @Autowired
    CustomerRepository clientRepository;

    public Customer addCustomer(CustomerRegistrationData data){
        if (clientRepository.existsByCpf(data.cpf())) {
            throw new DuplicateKeyException("Já existe um cliente com o mesmo cpf.");
        }
        if (clientRepository.existsByEmail(data.email())) {
            throw new DuplicateKeyException("Já existe um cliente com o mesmo e-mail.");
        }
        return clientRepository.save(new Customer(data));
    }

    public Page<listingDataCustomers> listCustomer(Pageable pageable) {
        return clientRepository.findAllByActiveTrue(pageable).map(listingDataCustomers::new);
    }

    public Customer listCustomerById(Long id) {
        Customer customer =  clientRepository.getReferenceByIdAndActiveTrue(id);
        if (customer == null){
            throw new EntityNotFoundException("Cliente não existe ou está inativado");
        }
        return customer;
    }

    public void updateCustomer(Long id, CustomerUploadData data) {
        Customer customer = clientRepository.getReferenceByIdAndActiveTrue(id);
        if (customer == null){
            throw new EntityNotFoundException("Cliente não existe ou está inativado");
        }
        customer.updateInformation(data);
    }
}