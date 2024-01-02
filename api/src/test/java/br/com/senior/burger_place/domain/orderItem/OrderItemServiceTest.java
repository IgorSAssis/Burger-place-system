package br.com.senior.burger_place.domain.orderItem;

import br.com.senior.burger_place.domain.orderItem.dto.ListOrderItemsDTO;
import br.com.senior.burger_place.utils.OrderItemTestFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderItemServiceTest {
    @Mock
    private OrderItemRepository orderItemRepository;
    @InjectMocks
    private OrderItemService orderItemService;

    @Test
    void listPendingOrderItems_whenStatusIsNull_shouldThrows() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> this.orderItemService.listPendingOrderItems(
                        Pageable.ofSize(20),
                        null
                )
        );

        assertEquals("Obrigatório informar um status", exception.getMessage());
        verifyNoInteractions(this.orderItemRepository);
    }

    @Test
    void listPendingOrderItems_whenStatusIsNotNull_shouldReturnPageWithListOrderItemsDTO() {
        List<OrderItem> someOrderItems = List.of(
                OrderItemTestFactory.orderItemFactory(1L),
                OrderItemTestFactory.orderItemFactory(2L)
        );

        PageImpl<OrderItem> orderItemsPage = new PageImpl<>(someOrderItems);

        when(this.orderItemRepository.findAllByActiveTrueAndStatusEquals(
                any(Pageable.class),
                any(OrderItemStatus.class)
        )).thenReturn(orderItemsPage);

        List<ListOrderItemsDTO> output = this.orderItemService
                .listPendingOrderItems(
                        Pageable.ofSize(20),
                        OrderItemStatus.RECEBIDO
                ).toList();

        List<ListOrderItemsDTO> expectedOutput = List.of(
                new ListOrderItemsDTO(someOrderItems.get(0)),
                new ListOrderItemsDTO(someOrderItems.get(1))
        );

        assertAll(
                () -> assertEquals(expectedOutput.size(), output.size()),
                () -> assertEquals(expectedOutput.get(0), output.get(0)),
                () -> assertEquals(expectedOutput.get(1), output.get(1))
        );
    }
}
