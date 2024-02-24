package br.com.senior.burger_place.domain.orderItem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static utils.OrderItemCreator.createOrderItem;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderItemService unit tests")
public class OrderItemServiceTest {
    @Mock
    private OrderItemRepository orderItemRepository;
    @InjectMocks
    private OrderItemService orderItemService;

    @Nested
    @DisplayName("listOrderItems tests")
    class ListOrderItemsTest {
        @Test
        void listOrderItems_whenCalled_shouldReturnPageWithOrderItem() {
            List<OrderItem> orderItems = List.of(createOrderItem(), createOrderItem());
            Page<OrderItem> orderItemPage = new PageImpl<>(orderItems, Pageable.ofSize(20), orderItems.size());

            mockFindAllByActiveTrueAndStatusEqual(orderItemPage);

            Page<OrderItem> output = orderItemService.listOrderItems(
                    orderItemPage.getPageable(), OrderItemStatus.EM_ANDAMENTO
            );

            assertAll(
                    () -> assertNotNull(output),
                    () -> assertEquals(orderItems.get(0), output.getContent().get(0)),
                    () -> assertEquals(orderItems.get(1), output.getContent().get(1))
            );
        }

        private void mockFindAllByActiveTrueAndStatusEqual(Page<OrderItem> expectedReturn) {
            when(
                    orderItemRepository.findAllByActiveTrueAndStatusEquals(any(Pageable.class), any(OrderItemStatus.class))
            ).thenReturn(expectedReturn);
        }
    }
}
