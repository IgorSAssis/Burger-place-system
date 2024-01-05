package br.com.senior.burger_place.domain.customer;

import br.com.senior.burger_place.domain.address.AdressDto;
import br.com.senior.burger_place.domain.customer.dto.CustomerRegistrationDTO;
import br.com.senior.burger_place.domain.customer.dto.CustomerUpdatedDTO;
import br.com.senior.burger_place.domain.customer.dto.ListingCustomersDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;
    @Mock
    private CustomerRepository customerRepository;
    @Captor
    private ArgumentCaptor<Customer> customerCaptor;

    @Test
    public void addCustomer_whenNotExistsAclientWithSameEmail_shouldSaveCustomer() {

        AdressDto adressDto = new AdressDto("Rua A", "Bairro A", "Cidade A", "Estado A", "88888888", null, null);
        CustomerRegistrationDTO dto = new CustomerRegistrationDTO("Ricardo Almeira", "Ricardo@email.com", "99999999900", adressDto);

        when(customerRepository.existsByCpf(dto.cpf())).thenReturn(false);
        when(customerRepository.existsByEmail(dto.email())).thenReturn(false);

        customerService.addCustomer(dto);
        verify(customerRepository, times(1)).save(customerCaptor.capture());

        Customer customerCaptured = customerCaptor.getValue();

        assertEquals(dto.name(), customerCaptured.getName());
        assertEquals(dto.cpf(), customerCaptured.getCpf());
        assertEquals(dto.email(), customerCaptured.getEmail());

        assertEquals(dto.address().neighborhood(), customerCaptured.getAddress().getNeighborhood());
        assertEquals(dto.address().city(), customerCaptured.getAddress().getCity());
        assertEquals(dto.address().streetAddress(), customerCaptured.getAddress().getStreetAddress());
        assertEquals(dto.address().state(), customerCaptured.getAddress().getState());
        assertEquals(dto.address().complement(), customerCaptured.getAddress().getComplement());
        assertEquals(dto.address().postalCode(), customerCaptured.getAddress().getPostalCode());
        assertEquals(dto.address().residentialNumber(), customerCaptured.getAddress().getResidentialNumber());
    }


    @Test
    public void addCustomer_whenExistsAclientWithSameCpf_shouldThrowException() {

        AdressDto adressDto = new AdressDto("Rua A", "Bairro A", "Cidade A", "Estado A", "88888888", null, null);
        CustomerRegistrationDTO dto = new CustomerRegistrationDTO("Ricardo Almeira", "Ricardo@email.com", "99999999900", adressDto);

        when(customerRepository.existsByCpf(dto.cpf())).thenReturn(true);
        DuplicateKeyException exception = assertThrows(DuplicateKeyException.class,
                () -> customerService.addCustomer(dto));

        assertEquals("Já existe um cliente com o mesmo cpf.", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }
    @Test
    public void addCustomer_whenTheCPFHasMoreThan11Digits_shouldThrowException() {

        AdressDto adressDto = new AdressDto("Rua A", "Bairro A", "Cidade A", "Estado A", "88888888", null, null);
        CustomerRegistrationDTO dto = new CustomerRegistrationDTO("Ricardo Almeira", "Ricardo@email.com", "999999999001233", adressDto);

        when(customerRepository.existsByCpf(dto.cpf())).thenReturn(false);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> customerService.addCustomer(dto));

        assertEquals("O CPF deve ter 11 dígitos", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    public void addCustomer_whenExistsAclientWithSameEmail_shouldThrowException() {

        AdressDto adressDto = new AdressDto("Rua A", "Bairro A", "Cidade A", "Estado A", "88888888", null, null);
        CustomerRegistrationDTO dto = new CustomerRegistrationDTO("Ricardo Almeira", "Ricardo@email.com", "99999999900", adressDto);

        when(customerRepository.existsByCpf(dto.cpf())).thenReturn(false);
        when(customerRepository.existsByEmail(dto.email())).thenReturn(true);
        DuplicateKeyException exception = assertThrows(DuplicateKeyException.class,
                () -> customerService.addCustomer(dto));

        assertEquals("Já existe um cliente com o mesmo e-mail.", exception.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    public void listCustomer_whenCustomerIsActive_shouldReturnInPagination() {
        AdressDto adressDto = new AdressDto("Rua A", "Bairro A", "Cidade A", "Estado A", "88888888", null, null);
        CustomerRegistrationDTO customer01 = new CustomerRegistrationDTO("Ricardo Almeira", "Ricardo@email.com", "99999999900", adressDto);
        CustomerRegistrationDTO customer02 = new CustomerRegistrationDTO("Rodrigo Alencar", "Rodrigo@email.com", "99999999911", adressDto);
        List<Customer> customersActiveList = List.of(new Customer(customer01), new Customer(customer02));

        when(customerRepository.findAllByActiveTrue(any(Pageable.class))).
                thenReturn(new PageImpl<>(customersActiveList));

        Pageable pageable = PageRequest.of(0, 10);
        Page<ListingCustomersDTO> result = customerService.listCustomer(pageable);

        verify(customerRepository, times(1)).findAllByActiveTrue(any(Pageable.class));

        assertEquals(2, result.getContent().size());
    }

    @Test
    public void listCustomerById_whenCustomerIsNotNull_shouldReturnCustomer(){
        Long customerId = 1l;
        CustomerRegistrationDTO customer01 = new CustomerRegistrationDTO("Ricardo Almeira", "Ricardo@email.com", "99999999900", mock(AdressDto.class));
        Customer customer = new Customer(customer01);

        when(customerRepository.getReferenceByIdAndActiveTrue(customerId)).thenReturn(customer);
        Customer result = customerService.listCustomerById(customerId);

        assertEquals(customer, result);
        verify(customerRepository, times(1)).getReferenceByIdAndActiveTrue(customerId);
    }
    @Test
    public void listCustomerById_whenCustomerIsNull_shouldThrowException(){
        Long customerId = 1l;

        when(customerRepository.getReferenceByIdAndActiveTrue(customerId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> customerService.listCustomerById(customerId));
        verify(customerRepository, times(1)).getReferenceByIdAndActiveTrue(customerId);
    }

    @Test
    public void updateCustomer_whenCustomerIsNull_shouldThrowException(){
        Long customerId = 1l;
        CustomerUpdatedDTO customerUploadDTO = new CustomerUpdatedDTO("Roberto de Assis", "Roberto@email.com", mock(AdressDto.class));

        when(customerRepository.getReferenceByIdAndActiveTrue(customerId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, ()-> customerService.updateCustomer(customerId, customerUploadDTO));
        verify(customerRepository, times(1)).getReferenceByIdAndActiveTrue(customerId);
    }

    @Test
    public void updateCustomer_whenCustomerIsNull_shouldReturnUpdatedCustomar(){
        Long customerId = 1l;
        CustomerUpdatedDTO customerUploadDTO = new CustomerUpdatedDTO("Roberto de Assis", "Roberto@email.com", mock(AdressDto.class));
        Customer existingCustomer = mock(Customer.class);

        when(customerRepository.getReferenceByIdAndActiveTrue(customerId)).thenReturn(existingCustomer);

        customerService.updateCustomer(customerId, customerUploadDTO);

        verify(existingCustomer, times(1)).updateInformation(customerUploadDTO);
        verify(customerRepository, times(1)).getReferenceByIdAndActiveTrue(customerId);
    }
}