package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.product.Product;
import br.com.senior.burger_place.domain.product.ProductCategory;
import br.com.senior.burger_place.domain.product.ProductConverter;
import br.com.senior.burger_place.domain.product.ProductService;
import br.com.senior.burger_place.domain.product.dto.CreateProductDTO;
import br.com.senior.burger_place.domain.product.dto.ProductDTO;
import br.com.senior.burger_place.domain.product.dto.UpdateProductDTO;
import br.com.senior.burger_place.infra.dto.ResponseWithFieldErrors;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static utils.ProductCreator.*;

@WebMvcTest(controllers = {ProductController.class})
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductController integration tests")
class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductController productController;
    @MockBean
    private ProductService productServiceMocked;
    @MockBean
    private ProductConverter productConverterMocked;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("list tests")
    class ListTest {
        @Test
        void list_whenExistProducts_shouldReturnStatus200AndPageWithProductDTO() throws Exception {
            List<Product> products = List.of(createProduct(), createProduct());
            Page<Product> productPage = new PageImpl<>(products, Pageable.ofSize(10), 10);

            mockListProducts(productPage);
            mockToProductDTO(products.get(0), createProductDTO());
            mockToProductDTO(products.get(1), createProductDTO());

            ResultActions response = mockMvc
                    .perform(MockMvcRequestBuilders.get("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .queryParam("name", "Hamburger")
                            .queryParam("price", "29.8")
                            .queryParam("ingredients", "tomate")
                            .queryParam("category", "BURGER")
                            .queryParam("active", "true")
                    );

            response
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath(
                            "$.content.size()",
                            CoreMatchers.is(products.size())
                    ))
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        void list_whenExistProducts_shouldReturnStatus200AndEmptyPage() throws Exception {
            Page<Product> productPage = new PageImpl<>(List.of(), Pageable.ofSize(10), 10);

            mockListProducts(productPage);

            ResultActions response = mockMvc
                    .perform(MockMvcRequestBuilders.get("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .queryParam("name", "Hamburger")
                            .queryParam("price", "29.8")
                            .queryParam("ingredients", "tomate")
                            .queryParam("category", "BURGER")
                            .queryParam("active", "true")
                    );

            response
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath(
                            "$.content.size()",
                            CoreMatchers.is(0)
                    ))
                    .andDo(MockMvcResultHandlers.print());
        }

        private void mockListProducts(Page<Product> expectedReturn) {
            Mockito.when(productServiceMocked.listProducts(
                    Mockito.any(Pageable.class),
                    Mockito.anyString(),
                    Mockito.anyDouble(),
                    Mockito.anyString(),
                    Mockito.any(ProductCategory.class),
                    Mockito.anyBoolean()
            )).thenReturn(expectedReturn);
        }
    }

    @Nested
    @DisplayName("show tests")
    class ShowTest {
        @Test
        void show_whenProductExists_shouldReturnStatus200AndProductDTO() throws Exception {
            Product product = createProduct();
            ProductDTO productDTO = createProductDTO();
            productDTO.setId(product.getId());

            mockShowProduct(product);
            mockToProductDTO(product, productDTO);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.get("/products/{id}", product.getId())
                            .contentType(MediaType.APPLICATION_JSON));

            response
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(result -> {
                        ProductDTO output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ProductDTO.class
                        );

                        assertEquals(productDTO, output);
                    })
                    .andDo(MockMvcResultHandlers.print());
        }

        private void mockShowProduct(Product expectedReturn) {
            Mockito.when(productServiceMocked.showProduct(Mockito.any(UUID.class)))
                    .thenReturn(expectedReturn);
        }
    }

    @Nested
    @DisplayName("create tests")
    class CreateTest {
        @ParameterizedTest
        @NullAndEmptySource
        void create_whenDTONameIsInvalid_shouldReturnStatus400WithError(String name) throws Exception {
            String field = "name";
            String expectedErrorMessage = "Product name cannot be null or blank";

            CreateProductDTO createProductDTO = createCreateProductDTO();
            createProductDTO.setName(name);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(createProductDTO))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(result -> {
                        ResponseWithFieldErrors output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ResponseWithFieldErrors.class
                        );

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(1, output.getErrors().size()),
                                () -> assertEquals(field, output.getErrors().get(0).getField()),
                                () -> assertEquals(expectedErrorMessage, output.getErrors().get(0).getMessage())

                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }

        @ParameterizedTest
        @NullAndEmptySource
        void create_whenDTOIngredientsIsInvalid_shouldReturnStatus400WithError(String ingredients) throws Exception {
            String field = "ingredients";
            String expectedErrorMessage = "Product ingredients cannot be null or blank";

            CreateProductDTO createProductDTO = createCreateProductDTO();
            createProductDTO.setIngredients(ingredients);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(createProductDTO))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(result -> {
                        ResponseWithFieldErrors output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ResponseWithFieldErrors.class
                        );

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(1, output.getErrors().size()),
                                () -> assertEquals(field, output.getErrors().get(0).getField()),
                                () -> assertEquals(expectedErrorMessage, output.getErrors().get(0).getMessage())

                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }

        @ParameterizedTest
        @CsvSource({
                ",Product price cannot be null",
                "0,Product price must be higher than zero",
                "-1,Product price must be higher than zero",
                "-10,Product price must be higher than zero"
        })
        void create_whenDTOPriceIsInvalid_shouldReturnStatus400WithError(Double price, String expectedErrorMessage) throws Exception {
            String field = "price";

            CreateProductDTO createProductDTO = createCreateProductDTO();
            createProductDTO.setPrice(price);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(createProductDTO))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(result -> {
                        ResponseWithFieldErrors output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ResponseWithFieldErrors.class
                        );

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(1, output.getErrors().size()),
                                () -> assertEquals(field, output.getErrors().get(0).getField()),
                                () -> assertEquals(expectedErrorMessage, output.getErrors().get(0).getMessage())

                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        void create_whenDTOCategoryIsInvalid_shouldReturnStatus400WithError() throws Exception {
            String field = "category";
            String expectedErrorMessage = "Product category cannot be null";

            CreateProductDTO createProductDTO = createCreateProductDTO();
            createProductDTO.setCategory(null);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(createProductDTO))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(result -> {
                        ResponseWithFieldErrors output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ResponseWithFieldErrors.class
                        );

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(1, output.getErrors().size()),
                                () -> assertEquals(field, output.getErrors().get(0).getField()),
                                () -> assertEquals(expectedErrorMessage, output.getErrors().get(0).getMessage())

                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        void create_whenDTOValid_shouldReturnStatus201WithProductDTO() throws Exception {
            CreateProductDTO createProductDTO = createCreateProductDTO();
            Product product = createProduct();
            ProductDTO productDTO = createProductDTO();
            productDTO.setId(product.getId());

            String expectedCreatedResource = String.format("/products/%s", product.getId().toString());

            mockCreateProduct(product);
            mockToProductDTO(product, productDTO);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(createProductDTO))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(result -> {
                        ProductDTO output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ProductDTO.class
                        );

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(productDTO, output),
                                () -> assertNotNull(result.getResponse().getRedirectedUrl()),
                                () -> assertTrue(result.getResponse().getRedirectedUrl().contains(expectedCreatedResource))

                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }

        private void mockCreateProduct(Product expectedReturn) {
            Mockito.when(productServiceMocked.createProduct(Mockito.any(CreateProductDTO.class)))
                    .thenReturn(expectedReturn);
        }
    }

    @Nested
    @DisplayName("update tests")
    class UpdateTest {
        @ParameterizedTest
        @ValueSource(doubles = {0, -1, -2})
        void update_whenPriceIsInvalid_shouldReturnStatus400WithError(Double price) throws Exception {
            String field = "price";
            String expectedErrorMessage = "Product price must be higher than zero";

            UpdateProductDTO updateProductDTO = createUpdateProductDTO();
            updateProductDTO.setPrice(price);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.put("/products/{id}", UUID.randomUUID().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(updateProductDTO))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(result -> {
                        ResponseWithFieldErrors output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ResponseWithFieldErrors.class
                        );

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(1, output.getErrors().size()),
                                () -> assertEquals(field, output.getErrors().get(0).getField()),
                                () -> assertEquals(expectedErrorMessage, output.getErrors().get(0).getMessage())

                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        void update_whenDTOIsValid_shouldReturnStatus200WithProductDTO() throws Exception {
            UpdateProductDTO updateProductDTO = createUpdateProductDTO();
            Product product = createProduct();
            ProductDTO productDTO = createProductDTO();
            productDTO.setId(product.getId());

            mockUpdateProduct(product);
            mockToProductDTO(product, productDTO);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.put("/products/{id}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(updateProductDTO))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(result -> {
                        ProductDTO output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ProductDTO.class
                        );

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(productDTO, output)

                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }

        private void mockUpdateProduct(Product expectedReturn) {
            Mockito.when(productServiceMocked.updateProduct(Mockito.any(UUID.class), Mockito.any(UpdateProductDTO.class)))
                    .thenReturn(expectedReturn);
        }
    }

    @Nested
    @DisplayName("inactivate tests")
    class InactivateTest {
        @Test
        void inactivate_whenCalled_shouldReturnStatus204() throws Exception {
            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.delete("/products/{id}", UUID.randomUUID().toString())
                            .contentType(MediaType.APPLICATION_JSON)
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isNoContent())
                    .andDo(MockMvcResultHandlers.print());
        }
    }


    private void mockToProductDTO(Product product, ProductDTO expectedReturn) {
        Mockito.when(productConverterMocked.toProductDTO(product)).thenReturn(expectedReturn);
    }
}