package br.com.senior.burger_place.domain.orderItem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> getReferenceByActiveTrueAndOccupationIdAndIdIn(UUID occupationId, List<UUID> orderItemIds);

    OrderItem getReferenceByIdAndOccupationIdAndActiveTrue(UUID id, UUID occupationId);

    List<OrderItem> findByOccupationId(UUID occupationId);

    Page<OrderItem> findAllByActiveTrueAndStatusEquals(Pageable pageable, OrderItemStatus status);

}
