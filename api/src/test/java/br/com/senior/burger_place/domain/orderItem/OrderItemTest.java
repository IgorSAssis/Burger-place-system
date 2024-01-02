package br.com.senior.burger_place.domain.orderItem;

import br.com.senior.burger_place.domain.occupation.dto.UpdateOrderItemDTO;
import br.com.senior.burger_place.utils.OrderItemTestFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class OrderItemTest {

    @Test
    void update_whenDTOObservationIsNotNull_shouldUpdateOrderItemObservation() {
        UpdateOrderItemDTO input = new UpdateOrderItemDTO(2, "Sem tomate e maionese");

        OrderItem someOrderItem = OrderItemTestFactory.orderItemFactory(1L);

        someOrderItem.update(input);

        assertEquals(input.observation(), someOrderItem.getObservation());
    }

    @Test
    void update_whenDTOObservationIsEmpty_shouldUpdateOrderItemObservation() {
        UpdateOrderItemDTO input = new UpdateOrderItemDTO(2, "");

        OrderItem someOrderItem = OrderItemTestFactory.orderItemFactory(1L);

        someOrderItem.update(input);

        assertNull(someOrderItem.getObservation());
    }

}
