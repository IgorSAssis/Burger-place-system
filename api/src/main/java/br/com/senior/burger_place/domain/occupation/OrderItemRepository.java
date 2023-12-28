package br.com.senior.burger_place.domain.occupation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> getReferenceByActiveTrueAndOccupationIdAndIdIn(Long occupationId, List<Long> orderItemIds);

    OrderItem getReferenceByIdAndOccupationIdAndActiveTrue(Long id, Long occupationId);

    List<OrderItem> findByOccupationId(Long occupationId);
}
