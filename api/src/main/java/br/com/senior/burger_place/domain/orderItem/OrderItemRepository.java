package br.com.senior.burger_place.domain.orderItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> getReferenceByActiveTrueAndOccupationIdAndIdIn(Long occupationId, List<Long> orderItemIds);

    OrderItem getReferenceByIdAndOccupationIdAndActiveTrue(Long id, Long occupationId);

    List<OrderItem> findByOccupationId(Long occupationId);

    Page<OrderItem> findAllByActiveTrueAndStatusEquals(Pageable pageable, OrderItemStatus status);

}
