package br.com.senior.burger_place.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAll(Specification<Product> specification, Pageable pageable);

    Page<Product> findAllByActiveTrue(Pageable pageable);

    Product getReferenceByIdAndActiveTrue(Long id);

    List<Product> getReferenceByActiveTrueAndIdIn(List<Long> ids);
}
