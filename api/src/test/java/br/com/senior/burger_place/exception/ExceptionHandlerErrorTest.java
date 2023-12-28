package br.com.senior.burger_place.exception;

import br.com.senior.burger_place.controller.ProductController;
import br.com.senior.burger_place.domain.product.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.NoSuchElementException;

@WebMvcTest(controllers = {ProductController.class})
@ExtendWith(MockitoExtension.class)
public class ExceptionHandlerErrorTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProductController productController;
    @MockBean
    private ProductService productService;

    @Test
    void handleNotFound_whenThrowsEntityNotFoundException_shouldReturnStatus404() throws Exception {
        Mockito.when(this.productService.showProduct(Mockito.anyLong())).thenThrow(EntityNotFoundException.class);

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
    void handleNotFound_whenThrowsDuplicateKeyException_shouldReturnStatus404() throws Exception {
        Mockito.when(this.productService.showProduct(Mockito.anyLong())).thenThrow(DuplicateKeyException.class);

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
    void handleBadRequests_whenThrowsNoSuchElementException_shouldReturnStatus400() throws Exception {
        Mockito.when(this.productService.showProduct(Mockito.anyLong())).thenThrow(NoSuchElementException.class);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/products/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void handleBadRequests_whenThrowsIllegalArgumentException_shouldReturnStatus400() throws Exception {
        Mockito.when(this.productService.showProduct(Mockito.anyLong())).thenThrow(IllegalArgumentException.class);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/products/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }
}
