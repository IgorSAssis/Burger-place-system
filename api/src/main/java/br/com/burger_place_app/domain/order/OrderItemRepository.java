package br.com.burger_place_app.domain.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderIdAndActiveTrue(Long orderId);

    @Query("""
            SELECT orderItem FROM OrderItem orderItem WHERE orderItem.order.id = ?1 AND orderItem.id IN ?2
            """)
    List<OrderItem> findOrderItems(Long orderId, List<Long> orderItemIds);

    OrderItem getReferenceByIdAndOrderId(Long id, Long orderId);
}
