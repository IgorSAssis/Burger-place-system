package br.com.senior.burger_place.domain.product;

import br.com.senior.burger_place.domain.product.dto.CreateProductDTO;
import br.com.senior.burger_place.domain.product.dto.UpdateProductDTO;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static utils.ProductCreator.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService unit tests")
public class ProductServiceTest {
    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepositoryMocked;
    @Mock
    private ProductSpecification productSpecificationMocked;

    @Nested
    @DisplayName("listProducts tests")
    class ListProductsTest {
        @Test
        void listProducts_whenExistProducts_shouldReturnPageWithProducts() {
            List<Product> products = List.of(createProduct());
            Page<Product> productPage = new PageImpl<>(products, Pageable.ofSize(10), products.size());

            mockApplyFilters(Specification.where(null));
            mockFindAll(productPage);

            List<Product> output = productService.listProducts(
                    productPage.getPageable(), null, null, null, null, null
            ).toList();

            assertAll(
                    () -> assertNotNull(output),
                    () -> assertEquals(products.size(), output.size()),
                    () -> assertEquals(products.get(0), output.get(0))
            );
        }

        @Test
        void listProducts_whenDoNotExistProducts_shouldReturnEmptyPage() {
            Page<Product> productPage = new PageImpl<>(List.of(), Pageable.ofSize(10), 0);

            mockApplyFilters(Specification.where(null));
            mockFindAll(productPage);

            List<Product> output = productService.listProducts(
                    productPage.getPageable(), null, null, null, null, null
            ).toList();

            assertAll(
                    () -> assertNotNull(output),
                    () -> assertTrue(output.isEmpty())
            );
        }

        private void mockApplyFilters(Specification<Product> expectedReturn) {
            when(productSpecificationMocked.applyFilters(null, null, null, null, null))
                    .thenReturn(expectedReturn);
        }

        private void mockFindAll(Page<Product> expectedReturn) {
            when(productRepositoryMocked.findAll(eq(Specification.where(null)), any(Pageable.class)))
                    .thenReturn(expectedReturn);
        }
    }

    @Nested
    @DisplayName("showProduct tests")
    class ShowProductTest {
        @Test
        void showProduct_whenIdIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.showProduct(null)
            );
        }

        @Test
        void showProduct_whenProductDoesNotExist_shouldEntityNotFoundException() {
            String expectedErrorMessage = "Product does not exist";

            mockFindById(null);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> productService.showProduct(UUID.randomUUID())
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void showProduct_whenProductExists_shouldReturnProduct() {
            Product product = createProduct();

            mockFindById(product);

            Product output = productService.showProduct(UUID.randomUUID());

            assertNotNull(output);
            assertEquals(product, output);
        }

        private void mockFindById(Product expectedReturn) {
            when(productRepositoryMocked.findById(any(UUID.class))).thenReturn(Optional.ofNullable(expectedReturn));
        }
    }

    @Nested
    @DisplayName("createProduct tests")
    class CreateProductTest {
        @Captor
        private ArgumentCaptor<Product> productArgumentCaptor;

        @Test
        void createProduct_whenDTOIsNull_shouldIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.createProduct(null)
            );
        }

        @Test
        void createProduct_whenDTOIsValid_shouldCreateAndReturnProduct() {
            CreateProductDTO createProductDTO = createCreateProductDTO();

            productService.createProduct(createProductDTO);
            verify(productRepositoryMocked, times(1)).save(productArgumentCaptor.capture());

            Product output = productArgumentCaptor.getValue();

            assertAll(
                    () -> assertNotNull(output),
                    () -> assertEquals(createProductDTO.getName(), output.getName()),
                    () -> assertEquals(createProductDTO.getPrice(), output.getPrice()),
                    () -> assertEquals(createProductDTO.getIngredients(), output.getIngredients()),
                    () -> assertEquals(createProductDTO.getCategory(), output.getCategory()),
                    () -> assertEquals(createProductDTO.getUrl(), output.getUrl()),
                    () -> assertTrue(output.getActive())
            );
        }
    }

    @Nested
    @DisplayName("updateProduct tests")
    class UpdateProductTest {
        @Test
        void updateProduct_whenIdIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.updateProduct(null, null)
            );
        }

        @Test
        void updateProduct_whenDTOIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.updateProduct(UUID.randomUUID(), null)
            );
        }

        @Test
        void updateProduct_whenCustomerDoesNotExist_shouldThrowEntityNotFoundException() {
            String expectedErrorMessage = "Product does not exists or is inactive";

            mockFindByIdAndActiveTrue(null);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> productService.updateProduct(UUID.randomUUID(), createUpdateProductDTO())
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void updateProduct_whenDTOIsValid_shouldUpdateProduct() {
            Product productSpy = spy(createProduct());
            UpdateProductDTO updateProductDTO = createUpdateProductDTO();

            mockFindByIdAndActiveTrue(productSpy);

            Product output = productService.updateProduct(UUID.randomUUID(), updateProductDTO);

            verify(productSpy, times(1)).update(
                    updateProductDTO.getName(),
                    updateProductDTO.getIngredients(),
                    updateProductDTO.getPrice(),
                    updateProductDTO.getCategory(),
                    updateProductDTO.getUrl()
            );
            assertAll(
                    () -> assertNotNull(output),
                    () -> assertEquals(updateProductDTO.getName(), output.getName()),
                    () -> assertEquals(updateProductDTO.getIngredients(), output.getIngredients()),
                    () -> assertEquals(updateProductDTO.getPrice(), output.getPrice()),
                    () -> assertEquals(updateProductDTO.getCategory(), output.getCategory()),
                    () -> assertEquals(updateProductDTO.getUrl(), output.getUrl())
            );
        }
    }

    @Nested
    @DisplayName("inactivateProduct tests")
    class InactivateProductTest {
        @Test
        void inactivateProduct_whenIdIsNull_shouldThrowIllegalArgumentException() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> productService.inactivateProduct(null)
            );
        }

        @Test
        void inactivateProduct_whenCustomerDoesNotExist_shouldThrowEntityNotFoundException() {
            String expectedErrorMessage = "Product does not exists or is inactive";

            mockFindByIdAndActiveTrue(null);

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> productService.inactivateProduct(UUID.randomUUID())
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void inactivateProduct_whenCustomerExists_shouldInactivateCustomer() {
            Product productSpy = spy(createProduct());

            mockFindByIdAndActiveTrue(productSpy);

            productService.inactivateProduct(UUID.randomUUID());

            verify(productSpy, times(1)).inactivate();
            assertFalse(productSpy.getActive());
        }
    }

    private void mockFindByIdAndActiveTrue(Product product) {
        when(productRepositoryMocked.findByIdAndActiveTrue(any(UUID.class))).thenReturn(Optional.ofNullable(product));
    }
}
