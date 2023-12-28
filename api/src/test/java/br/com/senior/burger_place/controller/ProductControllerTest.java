package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.product.ProductService;
import br.com.senior.burger_place.domain.product.dto.CreateProductDTO;
import br.com.senior.burger_place.domain.product.dto.ProductDTO;
import br.com.senior.burger_place.domain.product.dto.UpdateProductDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@WebMvcTest(controllers = {ProductController.class})
@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductController productController;
    @MockBean
    private ProductService productService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listProducts_whenProductsExist_shouldReturnStatus200WithProducts() throws Exception {
        List<ProductDTO> someProductDTOs = Arrays.asList(
                new ProductDTO(1L, "Hamburguer tradicional", 25.9, null),
                new ProductDTO(2L, "Hamburguer vegetariano", 21.9, null)
        );

        Page<ProductDTO> page = new PageImpl<>(
                someProductDTOs,
                Pageable.ofSize(20),
                10
        );

        Mockito.when(this.productService.listProducts(Mockito.any(Pageable.class)))
                .thenReturn(page);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("page", "1")
                                .queryParam("size", "10")
                );

        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()", CoreMatchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath(
                                "$.content[0].id",
                                CoreMatchers.is(someProductDTOs.get(0).id()),
                                Long.class
                        )
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                                "$.content[0].name",
                                CoreMatchers.is(someProductDTOs.get(0).name())
                        )
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                                "$.content[0].description",
                                CoreMatchers.is(someProductDTOs.get(0).description())
                        )
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                                "$.content[0].price",
                                CoreMatchers.is(someProductDTOs.get(0).price())
                        )
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                                "$.content[1].id",
                                CoreMatchers.is(someProductDTOs.get(1).id()),
                                Long.class
                        )
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                                "$.content[1].name",
                                CoreMatchers.is(someProductDTOs.get(1).name())
                        )
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                                "$.content[1].description",
                                CoreMatchers.is(someProductDTOs.get(1).description())
                        )
                )
                .andExpect(MockMvcResultMatchers.jsonPath(
                                "$.content[1].price",
                                CoreMatchers.is(someProductDTOs.get(1).price())
                        )
                )
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void showProduct_whenProductExists_shouldReturnStatus200WithProduct() throws Exception {
        ProductDTO productDTO = new ProductDTO(1L, "Hamburguer tradicional", 24.9, null);

        Mockito.when(this.productService.showProduct(Mockito.anyLong())).thenReturn(Optional.of(productDTO));

        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.get("/products/{id}", 1L).contentType(MediaType.APPLICATION_JSON));

        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(productDTO.id()), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(productDTO.name())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price", CoreMatchers.is(productDTO.price())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(productDTO.description())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void showProduct_whenProductDoesNotExist_shouldReturnStatus404() throws Exception {
        Mockito.when(this.productService.showProduct(Mockito.anyLong())).thenReturn(Optional.empty());

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/products/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                );

        response
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void createProduct_whenDTONameAndPriceIsNull_shouldReturnStatus400() throws Exception {
        CreateProductDTO input = new CreateProductDTO(null, null, null);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].field", CoreMatchers.is("name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", CoreMatchers.is("must not be blank")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].field", CoreMatchers.is("price")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].message", CoreMatchers.is("must not be null")))
                .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", " ", "  "})
    void createProduct_whenDTONameIsBlank_shouldReturnStatus400(String name) throws Exception {
        CreateProductDTO input = new CreateProductDTO(name, 23.5, null);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].field", CoreMatchers.is("name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", CoreMatchers.is("must not be blank")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void createProduct_whenDTOPriceIsNull_shouldReturnStatus400() throws Exception {
        CreateProductDTO input = new CreateProductDTO("Hamburger tradicional", null, null);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].field", CoreMatchers.is("price")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", CoreMatchers.is("must not be null")))
                .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, -10.0})
    void createProduct_whenDTOPriceInvalid_shouldReturnStatus400(Double price) throws Exception {
        CreateProductDTO input = new CreateProductDTO("Hamburger tradicional", price, null);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(input))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].field", CoreMatchers.is("price")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", CoreMatchers.is("must be greater than 0")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void createProduct_whenDTOIsValid_shouldReturnStatus201AndProductDTO() throws Exception {
        Long someProductId = 1L;
        String someProductName = "Hamburger tradicional";
        double someProductPrice = 23.65;
        String someProductDescription = "Pão com gergelim, hamburguer no ponto, alface, tomate e queijo";

        CreateProductDTO requestInput = new CreateProductDTO(
                someProductName,
                someProductPrice,
                someProductDescription
        );
        ProductDTO productDTO = new ProductDTO(
                someProductId,
                someProductName,
                someProductPrice,
                someProductDescription
        );

        Mockito.when(this.productService.createProduct(Mockito.any(CreateProductDTO.class))).thenReturn(productDTO);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestInput))
                );

        String expectedLocation = String.format("http://localhost/products/%d", someProductId);

        response
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(someProductId), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(someProductName)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price", CoreMatchers.is(someProductPrice)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(someProductDescription)))
                .andExpect(result -> Assertions.assertEquals(expectedLocation, result.getResponse().getRedirectedUrl()))
                .andExpect(result -> Assertions.assertNotNull(result.getResponse().getHeader("Location")))
                .andExpect(result -> Assertions.assertEquals(expectedLocation, result.getResponse().getHeader("Location")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void updateProduct_whenDTONameAndPriceIsNull_shouldReturnStatus400() throws Exception {
        UpdateProductDTO requestInput = new UpdateProductDTO(null, null, null);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/products/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestInput))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].field", CoreMatchers.is("name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", CoreMatchers.is("must not be blank")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].field", CoreMatchers.is("price")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].message", CoreMatchers.is("must not be null")))
                .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    void updateProduct_whenDTONameIsBlank_shouldReturnStatus400(String name) throws Exception {
        UpdateProductDTO requestInput = new UpdateProductDTO(name, 23.5, null);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/products/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestInput))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].field", CoreMatchers.is("name")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", CoreMatchers.is("must not be blank")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void updateProduct_whenDTOPriceIsNull_shouldReturnStatus400() throws Exception {
        UpdateProductDTO requestInput = new UpdateProductDTO("Hamburguer tradicional com porco", null, null);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/products/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestInput))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].field", CoreMatchers.is("price")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", CoreMatchers.is("must not be null")))
                .andDo(MockMvcResultHandlers.print());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, -10.0})
    void updateProduct_whenDTOPriceIsLessThanOrEqualToZero_shouldReturnStatus400(Double price) throws Exception {
        UpdateProductDTO requestInput = new UpdateProductDTO("Hamburguer tradicional com porco", price, null);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/products/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestInput))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].field", CoreMatchers.is("price")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].message", CoreMatchers.is("must be greater than 0")))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void updateProduct_whenDTOIsValid_shouldReturnStatus200AndProductDTO() throws Exception {
        Long someProductId = 1L;
        String someProductName = "Hamburguer tradicional com porco";
        double someProductPrice = 23.5;
        String someProductDescription = "Hamburguer especial com carne de porco assada";
        UpdateProductDTO requestInput = new UpdateProductDTO(someProductName, someProductPrice, someProductDescription);

        ProductDTO productDTO = new ProductDTO(someProductId, someProductName, someProductPrice, someProductDescription);

        Mockito.when(this.productService.updateProduct(Mockito.anyLong(), Mockito.any(UpdateProductDTO.class)))
                .thenReturn(productDTO);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/products/{id}", someProductId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestInput))
                );

        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(someProductId), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(someProductName)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price", CoreMatchers.is(someProductPrice)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is(someProductDescription)))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void inactivateProduct_whenProductExists_shouldReturnStatus204() throws Exception {
        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.delete("/products/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                );

        response
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }
}