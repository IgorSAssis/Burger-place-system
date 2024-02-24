package br.com.senior.burger_place.domain.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static utils.ProductCreator.createProduct;

@DisplayName("Product unit tests")
public class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        this.product = createProduct();
    }

    @Nested
    @DisplayName("inactivate tests")
    class InactivateTest {
        @Test
        void inactivate_whenProductIsInactive_shouldThrowIllegalStateException() {
            String expectedErrorMessage = "Product already inactive";

            product.inactivate();

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> product.inactivate()
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }
    }
}
