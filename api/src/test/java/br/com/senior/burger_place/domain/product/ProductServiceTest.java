package br.com.senior.burger_place.domain.product;

import br.com.senior.burger_place.domain.product.dto.CreateProductDTO;
import br.com.senior.burger_place.domain.product.dto.ProductDTO;
import br.com.senior.burger_place.domain.product.dto.UpdateProductDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static br.com.senior.burger_place.utils.ProductTestFactory.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    @Test
    void listProducts_whenExistProducts_shouldReturnPageWithProducts() {
        List<Product> someProducts = Arrays.asList(
                productFactory(1L),
                productFactory(2L)
        );

        Page<Product> somePage = new PageImpl<>(someProducts);

        when(this.productRepository.findAll(
                any(Specification.class),
                any(Pageable.class))
        ).thenReturn(somePage);

        List<ProductDTO> output = this.productService.listProducts(
                        Pageable.ofSize(20),
                        ProductCategory.BURGER)
                .toList();

        assertAll(
                () -> assertEquals(someProducts.size(), output.size()),
                () -> assertEquals(output.get(0).id(), someProducts.get(0).getId()),
                () -> assertEquals(output.get(0).name(), someProducts.get(0).getName()),
                () -> assertEquals(output.get(0).ingredients(), someProducts.get(0).getIngredients()),
                () -> assertEquals(output.get(0).price(), someProducts.get(0).getPrice()),
                () -> assertEquals(output.get(1).id(), someProducts.get(1).getId()),
                () -> assertEquals(output.get(1).name(), someProducts.get(1).getName()),
                () -> assertEquals(output.get(1).ingredients(), someProducts.get(1).getIngredients()),
                () -> assertEquals(output.get(1).price(), someProducts.get(1).getPrice())
        );
    }

    @Test
    void listProducts_whenExistProducts_shouldReturnPageWithoutProducts() {
        Page<Product> someEmptyPage = new PageImpl<>(new ArrayList<>());

        when(this.productRepository.findAll(
                any(Specification.class),
                any(Pageable.class))
        ).thenReturn(someEmptyPage);

        List<ProductDTO> output = this.productService.listProducts(
                        Pageable.ofSize(20),
                        ProductCategory.BURGER)
                .toList();

        assertTrue(output.isEmpty());
    }

    @Test
    void showProduct_whenProductExists_shouldReturnAnOptionalWithProduct() {
        Long someProductId = 1L;

        Product someProduct = productFactory(someProductId);

        when(this.productRepository.getReferenceByIdAndActiveTrue(anyLong()))
                .thenReturn(someProduct);

        Optional<ProductDTO> output = this.productService.showProduct(someProductId);

        assertAll(
                () -> assertFalse(output.isEmpty()),
                () -> assertEquals(output.get().id(), someProduct.getId()),
                () -> assertEquals(output.get().name(), someProduct.getName()),
                () -> assertEquals(output.get().ingredients(), someProduct.getIngredients()),
                () -> assertEquals(output.get().price(), someProduct.getPrice())
        );
    }

    @Test
    void showProduct_whenProductDoesNotExist_shouldReturnAnEmptyOptional() {
        Long someProductId = 1L;

        when(this.productRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(null);

        Optional<ProductDTO> output = this.productService.showProduct(someProductId);

        assertTrue(output.isEmpty());
    }

    @ParameterizedTest()
    @NullSource
    @ValueSource(longs = {0L, -1L})
    void showProduct_whenIdInvalid_shouldThrow(Long id) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.productService.showProduct(id)
        );

        assertEquals("ID inválida", exception.getMessage());
    }

    @Test
    void createProduct_whenDTOIsNull_shouldThrow() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.productService.createProduct(null)
        );

        assertEquals("DTO inválido", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n    "})
    void createProduct_whenDTONameIsInvalid_shouldThrow(String name) {
        CreateProductDTO input = createProductDTOFactory(name, null, null, null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.productService.createProduct(input)
        );

        assertEquals("nome inválido", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n    "})
    void createProduct_whenDTOIngredientsIsInvalid_shouldThrow(String ingredients) {
        CreateProductDTO input = createProductDTOFactory(
                "Hamburguer tradicional",
                ingredients,
                null,
                null
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.productService.createProduct(input)
        );

        assertEquals("ingredientes inválidos", exception.getMessage());
    }

    @Test
    void createProduct_whenDTOCategoryIsNull_shouldThrow() {
        CreateProductDTO input = createProductDTOFactory(
                "Hamburguer tradicional",
                "Pão, hamburguer, tomate, cebola, queijo, tomate",
                null,
                null
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.productService.createProduct(input)
        );

        assertEquals("categoria inválida", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(doubles = {0D, -1D, -10D})
    void createProduct_whenDTOPriceIsInvalid_shouldThrow(Double price) {
        CreateProductDTO input = createProductDTOFactory(
                "Hamburguer tradicional",
                "Pão, hamburguer, queijo, tomate, cebola, pepino, alface",
                ProductCategory.BURGER,
                price
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.productService.createProduct(input)
        );

        assertEquals("preço inválido", exception.getMessage());
    }

    @Test
    void createProduct_whenDTOIsValid_shouldSaveAndReturnProductDTO() {
        Product product = productFactory(1L);
        CreateProductDTO input = createProductDTOFactory(
                product.getName(),
                product.getIngredients(),
                product.getCategory(),
                product.getPrice()
        );

        ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);

        when(this.productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO output = this.productService.createProduct(input);

        verify(this.productRepository, atMostOnce()).save(any(Product.class));

        verify(this.productRepository).save(argumentCaptor.capture());
        Product capturedValue = argumentCaptor.getValue();

        assertAll(
                () -> assertEquals(input.name(), capturedValue.getName()),
                () -> assertEquals(input.ingredients(), capturedValue.getIngredients()),
                () -> assertEquals(input.price(), capturedValue.getPrice()),

                () -> assertEquals(product.getId(), output.id()),
                () -> assertEquals(product.getName(), output.name()),
                () -> assertEquals(product.getIngredients(), output.ingredients()),
                () -> assertEquals(product.getPrice(), output.price())
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void updateProduct_whenIdIsInvalid_shouldThrow(Long id) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.productService.updateProduct(id, null)
        );

        assertEquals("ID inválida", exception.getMessage());
    }

    @Test
    void updateProduct_whenDTOIsNull_shouldThrow() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.productService.updateProduct(1L, null)
        );

        assertEquals("DTO inválido", exception.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(doubles = {0D, -1D, -10D})
    void updateProduct_whenDTOPriceIsInvalid_shouldThrow(Double price) {
        UpdateProductDTO input = updateProductDTOFactory("Hamburguer duplo", price);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.productService.updateProduct(1L, input)
        );

        assertEquals("preço inválido", exception.getMessage());
    }

    @Test
    void updateProduct_whenProductDoesNotExist_shouldThrow() {
        UpdateProductDTO input = updateProductDTOFactory("Hamburguer duplo", 10.5);

        when(this.productRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.productService.updateProduct(1L, input)
        );

        assertEquals("Produto não existe ou foi inativado", exception.getMessage());
    }

    @Test
    void updateProduct_whenProductExists_shouldUpdateAndReturnProductDTO() {
        Long someProductId = 1L;
        Product product = productFactory(someProductId);
        UpdateProductDTO input = updateProductDTOFactory(
                product.getName(),
                product.getIngredients(),
                product.getPrice(),
                product.getCategory()
        );

        Product productSpy = spy(product);

        when(this.productRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(productSpy);

        ProductDTO output = this.productService.updateProduct(someProductId, input);

        verify(productSpy, atMostOnce()).update(any(UpdateProductDTO.class));
        assertAll(
                () -> assertEquals(product.getId(), output.id()),
                () -> assertEquals(product.getName(), output.name()),
                () -> assertEquals(product.getIngredients(), output.ingredients()),
                () -> assertEquals(product.getPrice(), output.price()),
                () -> assertEquals(product.getCategory(), output.category()),
                () -> assertEquals(product.getUrl(), output.url())
        );
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -1L, -10L})
    void deleteProduct_whenIdIsInvalid_shouldThrow(Long id) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.productService.deleteProduct(id)
        );

        assertEquals("ID inválida", exception.getMessage());
    }

    @Test
    void deleteProduct_whenProductDoesNotExist_shouldThrow() {
        when(this.productRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(null);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> this.productService.deleteProduct(1L)
        );

        assertEquals("Produto não existe ou foi inativado", exception.getMessage());
    }

    @Test
    void deleteProduct_whenProductExists_shouldInactivateProduct() {
        Long someProductId = 1L;
        Product productSpy = spy(productFactory(someProductId));
        when(this.productRepository.getReferenceByIdAndActiveTrue(anyLong())).thenReturn(productSpy);

        this.productService.deleteProduct(someProductId);

        verify(productSpy, atMostOnce()).inactivate();
        assertFalse(productSpy.isActive());
    }
}
