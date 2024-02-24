package br.com.senior.burger_place.domain.orderItem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static utils.OrderItemCreator.createOrderItem;

@DisplayName("OrderItem unit tests")
public class OrderItemTest {

    @Nested
    @DisplayName("inactivate tests")
    class InactivateTest {
        @Test
        void inactivate_whenOrderItemIsInactive_shouldThrowIllegalStateException() {
            String expectedErrorMessage = "Order item already inactive";

            OrderItem orderItem = createOrderItem();
            orderItem.inactivate();

            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    orderItem::inactivate
            );

            assertEquals(expectedErrorMessage, exception.getMessage());
        }

        @Test
        void inactivate_whenOrderItemIsActive_shouldInactivate() {
            OrderItem orderItem = createOrderItem();
            orderItem.inactivate();

            assertFalse(orderItem.isActive());
        }
    }

    @Nested
    @DisplayName("startPreparation tests")
    class StartPreparationTest {
        @Test
        void startPreparation_whenOrderItemIsReceived_shouldStartPreparation() {
            OrderItem orderItem = createOrderItem();
            orderItem.setStatus(OrderItemStatus.RECEBIDO);

            orderItem.startPreparation();

            assertEquals(OrderItemStatus.EM_ANDAMENTO, orderItem.getStatus());
        }
    }

    @Nested
    @DisplayName("finishPreparation tests")
    class FinishPreparationTest {
        @Test
        void startPreparation_whenOrderItemIsReceived_shouldStartPreparation() {
            OrderItem orderItem = createOrderItem();
            orderItem.setStatus(OrderItemStatus.EM_ANDAMENTO);

            orderItem.finishPreparation();

            assertEquals(OrderItemStatus.PRONTO, orderItem.getStatus());
        }
    }

    @Nested
    @DisplayName("cancel tests")
    class CancelTest {
        @Test
        void startPreparation_whenOrderItemIsReceived_shouldStartPreparation() {
            OrderItem orderItem = createOrderItem();
            orderItem.setStatus(OrderItemStatus.EM_ANDAMENTO);

            orderItem.cancel();

            assertEquals(OrderItemStatus.CANCELADO, orderItem.getStatus());
        }
    }

    @Nested
    @DisplayName("deliver tests")
    class DeliverTest {
        @Test
        void startPreparation_whenOrderItemIsReceived_shouldStartPreparation() {
            OrderItem orderItem = createOrderItem();
            orderItem.setStatus(OrderItemStatus.EM_ANDAMENTO);

            orderItem.deliver();

            assertEquals(OrderItemStatus.ENTREGUE, orderItem.getStatus());
        }
    }
}
