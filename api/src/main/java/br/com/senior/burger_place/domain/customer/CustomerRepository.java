package br.com.senior.burger_place.domain.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer getReferenceByIdAndActiveTrue(Long id);

    Page<Customer> findAllByActiveTrue(Pageable pageable);

    @Query("""
            SELECT customer
            FROM Customer customer
            WHERE customer.id IN ?1
            AND   customer.active = true
            """)
    Set<Customer> getCustomers(Set<Long> customersId);

    boolean existsByCpf(String name);

    boolean existsByEmail(String email);
}
