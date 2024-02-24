package br.com.senior.burger_place.domain.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {
    Page<Board> findAll(Specification<Board> specification, Pageable pageable);

    Optional<Board> findByIdAndActiveTrue(UUID id);

    Board getReferenceByIdAndActiveTrue(UUID id);

    boolean existsByNumberAndActiveTrue(Integer number);

    boolean existsByNumberAndActiveTrueAndIdNot(Integer number, UUID id);

    @Query(
            nativeQuery = true,
            value =
                    """
                            SELECT EXISTS (
                                SELECT 1
                                FROM boards b
                                WHERE b.id = :id
                                AND b.id IN (
                                    SELECT o.board_id
                                    FROM occupations o
                                    WHERE o.end_occupation IS NULL
                                    AND o.active IS TRUE
                                )
                                AND b.active IS TRUE
                            );
                            """)
    boolean isBoardOccupied(UUID id);
}

