package br.com.senior.burger_place.domain.customer;

import br.com.senior.burger_place.domain.customer.dto.CustomerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.CustomerCreator.createCustomer;

@DisplayName("CustomerConverter unit tests")
public class CustomerConverterTest {
    private final CustomerConverter customerConverter = new CustomerConverter();
    private Customer customer;

    @BeforeEach
    void setUp() {
        this.customer = createCustomer();
    }

    @Test
    void toCustomerDTO_whenCalled_shouldConvertToCustomerDTO() {
        CustomerDTO output = this.customerConverter.toCustomerDTO(this.customer);

        assertAll(
                () -> assertEquals(this.customer.getId(), output.getId()),
                () -> assertEquals(this.customer.getName(), output.getName()),
                () -> assertEquals(this.customer.getEmail(), output.getEmail()),
                () -> assertEquals(this.customer.getCpf(), output.getCpf()),
                () -> assertEquals(this.customer.getActive(), output.getActive())
        );
    }
}
