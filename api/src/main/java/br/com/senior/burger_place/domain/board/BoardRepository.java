package br.com.senior.burger_place.domain.board;

import br.com.senior.burger_place.domain.board.dto.ListingBoardDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {
    Page<Board> findByLocationAndActiveTrue(BoardLocation location, Pageable pageable);
    Page<Board> findByLocationAndCapacityAndActiveTrue(BoardLocation boardLocation, Integer capacity, Pageable pageable);
    Page<Board> findByCapacityAndActiveTrue(Integer capacity, Pageable pageable);
    Board getReferenceByIdAndActiveTrue(Long id);


    @Query(
            nativeQuery = true,
            value = """
                SELECT *
                FROM boards b
                WHERE b.active IS TRUE
                AND NOT EXISTS (
                    SELECT 1
                    FROM occupations o
                    WHERE o.board_id = b.id
                    AND o.end_occupation IS NULL
                )
                """
    )
    Page<Board> findAllBoardsAvailable(Pageable pageable);

    boolean existsByNumber(int number);

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
    boolean isBoardOccupied(Long id);
}

