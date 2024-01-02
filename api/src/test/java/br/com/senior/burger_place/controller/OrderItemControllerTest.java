package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.orderItem.OrderItemService;
import br.com.senior.burger_place.domain.orderItem.OrderItemStatus;
import br.com.senior.burger_place.domain.orderItem.dto.ListOrderItemsDTO;
import br.com.senior.burger_place.utils.OrderItemTestFactory;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

@WebMvcTest(controllers = OrderItemController.class)
@ExtendWith(MockitoExtension.class)
public class OrderItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrderItemController orderItemController;
    @MockBean
    private OrderItemService orderItemService;

    @Test
    void listOrderItemsByStatus_whenStatusIsNull_shouldReturnStatus200WithOrderItems() throws Exception {
        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/order-items")
                                .contentType(MediaType.APPLICATION_JSON)
                );

        response
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    void listOrderItemsByStatus_whenStatusIsNow_shouldReturnStatus200WithOrderItems() throws Exception {
        List<ListOrderItemsDTO> someListOrderItemsDTOs = List.of(
                new ListOrderItemsDTO(OrderItemTestFactory.orderItemFactory(1L)),
                new ListOrderItemsDTO(OrderItemTestFactory.orderItemFactory(2L))
        );

        Page<ListOrderItemsDTO> page = new PageImpl<>(someListOrderItemsDTOs, Pageable.ofSize(5), 20);

        Mockito.when(
                this.orderItemService.listPendingOrderItems(
                        Mockito.any(Pageable.class),
                        Mockito.any(OrderItemStatus.class)
                )
        ).thenReturn(page);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/order-items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .queryParam("status", OrderItemStatus.ENTREGUE.name())
                );

        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.content.size()",
                        CoreMatchers.is(2)
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.content[0].id",
                        CoreMatchers.is(someListOrderItemsDTOs.get(0).id()),
                        Long.class
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.content[0].productName",
                        CoreMatchers.is(someListOrderItemsDTOs.get(0).productName())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.content[0].ingredients",
                        CoreMatchers.is(someListOrderItemsDTOs.get(0).ingredients())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.content[0].amount",
                        CoreMatchers.is(someListOrderItemsDTOs.get(0).amount())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.content[0].observation",
                        CoreMatchers.is(someListOrderItemsDTOs.get(0).observation())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.content[0].boardNumber",
                        CoreMatchers.is(someListOrderItemsDTOs.get(0).boardNumber())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.content[0].status",
                        CoreMatchers.is(someListOrderItemsDTOs.get(0).status().name())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.content[0].occupationId",
                        CoreMatchers.is(someListOrderItemsDTOs.get(0).occupationId()),
                        Long.class
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.content[1].id",
                        CoreMatchers.is(someListOrderItemsDTOs.get(1).id()),
                        Long.class
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.content[1].productName",
                        CoreMatchers.is(someListOrderItemsDTOs.get(1).productName())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.content[1].ingredients",
                        CoreMatchers.is(someListOrderItemsDTOs.get(1).ingredients())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.content[1].amount",
                        CoreMatchers.is(someListOrderItemsDTOs.get(1).amount())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.content[1].observation",
                        CoreMatchers.is(someListOrderItemsDTOs.get(1).observation())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.content[1].boardNumber",
                        CoreMatchers.is(someListOrderItemsDTOs.get(1).boardNumber())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.content[1].status",
                        CoreMatchers.is(someListOrderItemsDTOs.get(1).status().name())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.content[1].occupationId",
                        CoreMatchers.is(someListOrderItemsDTOs.get(1).occupationId()),
                        Long.class
                ))
                .andDo(MockMvcResultHandlers.print());

    }
}
