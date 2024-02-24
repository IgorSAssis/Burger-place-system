package br.com.senior.burger_place.domain.orderItem;

import br.com.senior.burger_place.domain.orderItem.dto.OrderItemDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static utils.OrderItemCreator.createOrderItem;

@DisplayName("OrderItemConverter unit tests")
public class OrderItemConverterTest {
    private final OrderItemConverter orderItemConverter = new OrderItemConverter();
    private OrderItem item;

    @BeforeEach
    void setUp() {
        this.item = createOrderItem();
    }

    @Test
    void toOrderItemsDTO_whenCalled_shouldConvertToOrderItemDTO() {
        OrderItemDTO output = this.orderItemConverter.toOrderItemsDTO(this.item);

        assertAll(
                () -> assertEquals(this.item.getId(), output.getId()),
                () -> assertEquals(this.item.getProduct().getName(), output.getProductName()),
                () -> assertEquals(this.item.getProduct().getIngredients(), output.getIngredients()),
                () -> assertEquals(this.item.getAmount(), output.getAmount()),
                () -> assertEquals(this.item.getObservation(), output.getObservation()),
                () -> assertEquals(this.item.getOccupation().getBoard().getNumber(), output.getBoardNumber()),
                () -> assertEquals(this.item.getStatus(), output.getStatus()),
                () -> assertEquals(this.item.getOccupation().getId(), output.getOccupationId())
        );
    }
}
