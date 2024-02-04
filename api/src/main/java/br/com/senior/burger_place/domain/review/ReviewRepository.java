package br.com.senior.burger_place.domain.review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {

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
    boolean verifyOccupationExists(UUID id);
}
