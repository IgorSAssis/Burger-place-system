package br.com.senior.burger_place.domain.review.topicReview;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TopicReviewRepository extends JpaRepository<TopicReview, Long> {

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

    Page<TopicReview> findByCategory(Category category, Pageable pageable);

    List<TopicReview> findByReviewId(Long reviewId);
}
