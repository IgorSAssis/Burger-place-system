package br.com.burger_place_app.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByActiveTrue(Pageable id);
    Product getReferenceByIdAndActiveTrue(Long id);
    @Query("SELECT p FROM Product p WHERE p.id IN ?1")
    List<Product> getProductPriceById(List<Long> ids);
}
