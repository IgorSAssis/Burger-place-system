package br.com.burger_place_app.domain.customer;

import br.com.burger_place_app.domain.customer.dto.CustomerRegistrationData;
import br.com.burger_place_app.domain.customer.dto.CustomerUploadData;
import br.com.burger_place_app.domain.customer.dto.listingDataCustomers;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository clientRepository;

    public Customer addCustomer(CustomerRegistrationData data){
        return clientRepository.save(new Customer(data));

    }

    public Page<listingDataCustomers> listCustomer(Pageable pageable) {
        return clientRepository.findAll(pageable).map(listingDataCustomers::new);
    }
// COMENTEI PARA TESTAR SE DE FATO É DESNECESSÁRIO. A PRINCIPIO CONSIGO TRATAR TUDO DIRETANENTE NO HANDLER
    public Customer listCustomerById(Long id) {
        return clientRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Cliente não cadastrado"));
    }

    public void deleteCustomer(Long id) {
//        if (clientRepository.findById(id).isEmpty()){
//            throw new NoSuchElementException();
//        }
        clientRepository.deleteById(id);
    }

    public void updateCustomer(Long id, CustomerUploadData data) {
        Customer customer = clientRepository.getReferenceByIdAndActiveTrue(id);
        if (customer == null){
            throw new EntityNotFoundException("Cliente não existe ou está inativado");
        }
            customer.updateInformation(data);
    }
}
