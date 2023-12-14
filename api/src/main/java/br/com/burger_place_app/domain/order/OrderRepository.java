package br.com.burger_place_app.domain.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAll(Pageable pageable);

    Page<Order> findAll(Specification<Order> specification, Pageable pageable);

    Order getReferenceByIdAndActiveTrue(Long id);

    boolean existsByIdAndActiveTrue(Long id);
}
