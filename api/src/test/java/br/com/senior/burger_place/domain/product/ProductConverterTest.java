package br.com.senior.burger_place.domain.product;

import br.com.senior.burger_place.domain.product.dto.ProductDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static utils.ProductCreator.createProduct;

@DisplayName("ProductConverter unit tests")
public class ProductConverterTest {

    private final ProductConverter productConverter = new ProductConverter();

    private Product product;

    @BeforeEach
    void setUp() {
        this.product = createProduct();
    }

    @Test
    void toProductDTO_whenCalled_shouldConvertToProductDTO() {
        ProductDTO output = productConverter.toProductDTO(this.product);

        assertAll(
                () -> assertNotNull(output),
                () -> assertEquals(this.product.getId(), output.getId()),
                () -> assertEquals(this.product.getName(), output.getName()),
                () -> assertEquals(this.product.getPrice(), output.getPrice()),
                () -> assertEquals(this.product.getCategory(), output.getCategory()),
                () -> assertEquals(this.product.getIngredients(), output.getIngredients()),
                () -> assertEquals(this.product.getUrl(), output.getUrl()),
                () -> assertEquals(this.product.getActive(), output.getActive())
        );

    }
}
