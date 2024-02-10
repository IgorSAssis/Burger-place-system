package br.com.senior.burger_place.domain.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Page<Customer> findAll(Specification<Customer> specification, Pageable pageable);

    Optional<Customer> findByIdAndActiveTrue(UUID id);

    @Query("""
            SELECT customer
            FROM Customer customer
            WHERE customer.id IN ?1
            AND   customer.active = true
            """)
    Set<Customer> getCustomers(Set<UUID> customersId);

    boolean existsByCpf(String cpf);

    boolean existsByEmail(String email);

    boolean existsByActiveTrueAndEmailEqualsAndIdNot(String email, UUID id);
}
