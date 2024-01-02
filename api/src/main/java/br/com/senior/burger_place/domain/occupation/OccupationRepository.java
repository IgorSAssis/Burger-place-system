package br.com.senior.burger_place.domain.occupation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OccupationRepository extends JpaRepository<Occupation, Long> {
    Page<Occupation> findAllByActiveTrue(Pageable pageable);
    Occupation getReferenceByIdAndActiveTrue(Long id);
    boolean existsByIdAndActiveTrue(Long id);

    Occupation findFirstByBoardIdOrderByBeginOccupationDesc(Long boardId);
}
