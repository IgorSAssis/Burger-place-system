package br.com.senior.burger_place.domain.review.topicReview;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface TopicReviewRepository extends JpaRepository<TopicReview, UUID> {

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

    Page<TopicReview> findByCategory(Category category, Pageable pageable);

    List<TopicReview> findByReviewId(UUID reviewId);
}
