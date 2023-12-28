package br.com.senior.burger_place.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByActiveTrue(Pageable id);
    Product getReferenceByIdAndActiveTrue(Long id);
    List<Product> getReferenceByActiveTrueAndIdIn(List<Long> ids);
}
