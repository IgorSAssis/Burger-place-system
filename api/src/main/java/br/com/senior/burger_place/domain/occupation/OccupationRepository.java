package br.com.senior.burger_place.domain.occupation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OccupationRepository extends JpaRepository<Occupation, UUID> {
    Page<Occupation> findAll(Specification<Occupation> specification, Pageable pageable);

    Page<Occupation> findAllByActiveTrue(Pageable pageable);

    Occupation getReferenceByIdAndActiveTrue(UUID id);

    boolean existsByIdAndActiveTrue(UUID id);

    Occupation findFirstByBoardIdOrderByBeginOccupationDesc(UUID boardId);
}
