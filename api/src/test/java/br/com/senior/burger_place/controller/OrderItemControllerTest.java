package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.orderItem.OrderItem;
import br.com.senior.burger_place.domain.orderItem.OrderItemConverter;
import br.com.senior.burger_place.domain.orderItem.OrderItemService;
import br.com.senior.burger_place.domain.orderItem.OrderItemStatus;
import br.com.senior.burger_place.domain.orderItem.dto.OrderItemDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

import static utils.OrderItemCreator.createOrderItem;
import static utils.OrderItemCreator.createOrderItemDTO;

@WebMvcTest(controllers = {OrderItemController.class})
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderItemController integration tests")
public class OrderItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrderItemController orderItemController;
    @MockBean
    private OrderItemService orderItemServiceMocked;
    @MockBean
    private OrderItemConverter orderItemConverterMocked;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("listByStatus tests")
    class ListByStatusTest {
        @Test
        void listByStatus_whenStatusHasBeenInformed_shouldReturnStatus200WithPageOfOrderItemDTO() throws Exception {
            OrderItem orderItem = createOrderItem();
            OrderItemDTO orderItemDTO = createOrderItemDTO();
            orderItemDTO.setId(orderItem.getId());

            List<OrderItem> orderItems = List.of(orderItem);
            Page<OrderItem> orderItemPage = new PageImpl<>(orderItems, Pageable.ofSize(20), orderItems.size());

            mockListOrderItems(orderItemPage);
            mockToOrderItemDTO(orderItem, orderItemDTO);

            ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/order-items")
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("status", OrderItemStatus.ENTREGUE.name())
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath(
                            "$.content.size()",
                            CoreMatchers.is(orderItemPage.getContent().size()))
                    )
                    .andDo(MockMvcResultHandlers.print());
        }


        private void mockListOrderItems(Page<OrderItem> expectedReturn) {
            Mockito.when(
                    orderItemServiceMocked.listOrderItems(Mockito.any(Pageable.class), Mockito.any(OrderItemStatus.class))
            ).thenReturn(expectedReturn);
        }
    }

    private void mockToOrderItemDTO(OrderItem item, OrderItemDTO expectedReturn) {
        Mockito.when(
                orderItemConverterMocked.toOrderItemsDTO(item)
        ).thenReturn(expectedReturn);
    }
}
