package br.com.senior.burger_place.domain.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static utils.CustomerCreator.createCustomer;

@DisplayName("Customer unit tests")
class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        this.customer = createCustomer();
    }

    @Nested
    @DisplayName("inactivate tests")
    class InactivateTest {
        @Test
        void inactivate_whenCustomerIsInactive_shouldThrowIllegalStateException() {
            String expectedErrorMessage = "Customer already inactive";

            customer.inactivate();

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> customer.inactivate()
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void inactivate_whenCustomerIsNotInactive_shouldInactivateCustomer() {
            customer.inactivate();

            assertFalse(customer.getActive());
        }
    }

}