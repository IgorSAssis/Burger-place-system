package br.com.senior.burger_place.domain.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query(
            nativeQuery = true,
            value =
                    """
                    SELECT EXISTS (
                        SELECT 1
                        FROM occupations o
                        WHERE o.id = :id
                    );
                    """
    )
    boolean verifyOccupationExists(Long id);
}
