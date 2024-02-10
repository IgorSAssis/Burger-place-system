package br.com.senior.burger_place.domain.customer;

import br.com.senior.burger_place.domain.customer.dto.CreateCustomerDTO;
import br.com.senior.burger_place.domain.customer.dto.UpdateCustomerDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import utils.CustomerCreator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static utils.CustomerCreator.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService unit tests")
class CustomerServiceTest {
    @InjectMocks
    private CustomerService customerService;
    @Mock
    private CustomerRepository customerRepositoryMocked;
    @Mock
    private CustomerSpecification customerSpecificationMocked;

    @Nested
    @DisplayName("listCustomers tests")
    class ListCustomersTest {
        @Test
        void listCustomers_whenExistCustomers_shouldReturnPageWithCustomers() {
            List<Customer> customers = List.of(createCustomer());
            Page<Customer> customerPage = new PageImpl<>(customers, Pageable.ofSize(10), 1);

            mockApplyFilters(Specification.where(null));
            mockFindAll(customerPage);

            List<Customer> output = customerService.listCustomers(
                    customerPage.getPageable(), null, null, null
            ).toList();

            assertAll(
                    () -> assertNotNull(output),
                    () -> assertEquals(1, output.size()),
                    () -> assertEquals(customers.get(0), output.get(0))
            );
        }

        @Test
        void listCustomers_whenDoNotExistCustomers_shouldReturnEmptyPage() {
            Page<Customer> customerPage = new PageImpl<>(List.of(), Pageable.ofSize(10), 1);

            mockApplyFilters(Specification.where(null));
            mockFindAll(customerPage);

            List<Customer> output = customerService.listCustomers(
                    customerPage.getPageable(), null, null, null
            ).toList();

            assertAll(
                    () -> assertNotNull(output),
                    () -> assertTrue(output.isEmpty())
            );
        }

        private void mockApplyFilters(Specification<Customer> expectedReturn) {
            when(customerSpecificationMocked.applyFilters(null, null, null))
                    .thenReturn(expectedReturn);
        }

        private void mockFindAll(Page<Customer> expectedReturn) {
            when(customerRepositoryMocked.findAll(eq(Specification.where(null)), any(Pageable.class)))
                    .thenReturn(expectedReturn);
        }
    }

    @Nested
    @DisplayName("showCustomer tests")
    class ShowCustomerTest {
        @Test
        void showCustomer_whenIdIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> customerService.showCustomer(null)
            );
        }

        @Test
        void showCustomer_whenCustomerDoesNotExist_shouldThrowEntityNotFoundException() {
            String expectedErrorMessage = "Customer does not exist";

            mockFindById(null);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> customerService.showCustomer(UUID.randomUUID())
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void showCustomer_whenCustomerExists_shouldReturnCustomer() {
            Customer customer = createCustomer();

            mockFindById(customer);

            Customer output = customerService.showCustomer(UUID.randomUUID());

            assertAll(
                    () -> assertNotNull(output),
                    () -> assertEquals(customer, output)
            );
        }

        private void mockFindById(Customer customer) {
            when(customerRepositoryMocked.findById(any(UUID.class)))
                    .thenReturn(Optional.ofNullable(customer));
        }
    }

    @Nested
    @DisplayName("createCustomer tests")
    class CreateCustomerTest {
        @Captor
        private ArgumentCaptor<Customer> customerArgumentCaptor;

        @Test
        void createCustomer_whenDTOIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> customerService.createCustomer(null)
            );
        }

        @Test
        void createCustomer_whenDTOCpfIsAlreadyRegistered_shouldThrowIllegalArgumentException() {
            String expectedErrorMessage = "CPF already registered";
            CreateCustomerDTO dto = createCreateCustomerDTO();

            mockExistsByCpf(true);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> customerService.createCustomer(dto)
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void createCustomer_whenDTOEmailIsAlreadyRegistered_shouldThrowIllegalArgumentException() {
            String expectedErrorMessage = "Email already registered";
            CreateCustomerDTO dto = createCreateCustomerDTO();

            mockExistsByCpf(false);
            mockExistsByEmail(true);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> customerService.createCustomer(dto)
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void createCustomer_whenDTOisValid_shouldCreateCustomer() {
            CreateCustomerDTO dto = createCreateCustomerDTO();

            mockExistsByCpf(false);
            mockExistsByEmail(false);

            customerService.createCustomer(dto);
            verify(customerRepositoryMocked, times(1)).save(customerArgumentCaptor.capture());

            Customer output = customerArgumentCaptor.getValue();

            assertAll(
                    () -> assertNotNull(output),
                    () -> assertEquals(dto.getName(), output.getName()),
                    () -> assertEquals(dto.getEmail(), output.getEmail()),
                    () -> assertEquals(dto.getCpf(), output.getCpf()),
                    () -> assertTrue(output.getActive())
            );
        }

        private void mockExistsByCpf(Boolean expectedReturn) {
            when(customerRepositoryMocked.existsByCpf(anyString())).thenReturn(expectedReturn);
        }

        private void mockExistsByEmail(Boolean expectedReturn) {
            when(customerRepositoryMocked.existsByEmail(anyString())).thenReturn(expectedReturn);
        }
    }

    @Nested
    @DisplayName("updateCustomer tests")
    class UpdateCustomerTest {
        @Test
        void updateCustomer_whenIdIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> customerService.updateCustomer(null, null)
            );
        }

        @Test
        void updateCustomer_whenDTOIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> customerService.updateCustomer(UUID.randomUUID(), null)
            );
        }

        @Test
        void updateCustomer_whenCustomerDoesNotExist_shouldThrowEntityNotFoundException() {
            UpdateCustomerDTO dto = createUpdateCustomerDTO();
            String expectedErrorMessage = "Customer does not exists or is inactive";

            mockFindActiveCustomerById(null);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> customerService.updateCustomer(UUID.randomUUID(), dto)
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void updateCustomer_whenEmailIsAlreadyInUse_shouldThrowIllegalArgumentException() {
            UpdateCustomerDTO dto = createUpdateCustomerDTO();
            Customer customer = createCustomer();
            String expectedErrorMessage = "Email already in use";

            mockFindActiveCustomerById(customer);
            mockExistsByActiveTrueAndEmailEqualsAndIdNot(true);

            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> customerService.updateCustomer(UUID.randomUUID(), dto)
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void updateCustomer_whenDTOIsValid_shouldUpdateCustomer() {
            UpdateCustomerDTO dto = createUpdateCustomerDTO();
            Customer customerSpy = spy(createCustomer());

            mockFindActiveCustomerById(customerSpy);
            mockExistsByActiveTrueAndEmailEqualsAndIdNot(false);

            Customer output = customerService.updateCustomer(UUID.randomUUID(), dto);

            verify(customerSpy, times(1)).update(dto.getName(), dto.getEmail());
            assertAll(
                    () -> assertNotNull(output),
                    () -> assertEquals(dto.getName(), output.getName()),
                    () -> assertEquals(dto.getEmail(), output.getEmail())
            );
        }

        private void mockExistsByActiveTrueAndEmailEqualsAndIdNot(Boolean expectedReturn) {
            when(customerRepositoryMocked.existsByActiveTrueAndEmailEqualsAndIdNot(anyString(), any(UUID.class)))
                    .thenReturn(expectedReturn);
        }
    }

    @Nested
    @DisplayName("inactivateCustomer tests")
    class InactivateCustomerTest {
        @Test
        void inactivateCustomer_whenIdIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> customerService.inactivateCustomer(null)
            );
        }

        @Test
        void inactivateCustomer_whenCustomerDoesNotExist_shouldThrowEntityNotFoundException() {
            String expectedErrorMessage = "Customer does not exists or is inactive";

            mockFindActiveCustomerById(null);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> customerService.inactivateCustomer(UUID.randomUUID())
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void inactivateCustomer_whenCustomerExists_shouldInactivate() {
            Customer customerSpy = spy(createCustomer());

            mockFindActiveCustomerById(customerSpy);

            customerService.inactivateCustomer(UUID.randomUUID());

            verify(customerSpy, times(1)).inactivate();
            assertFalse(customerSpy.getActive());
        }
    }

    private void mockFindActiveCustomerById(Customer expectedReturn) {
        when(customerRepositoryMocked.findByIdAndActiveTrue(any(UUID.class)))
                .thenReturn(Optional.ofNullable(expectedReturn));
    }
}