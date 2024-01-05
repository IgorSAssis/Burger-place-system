package br.com.senior.burger_place.domain.review.topicReview.dto;

import br.com.senior.burger_place.domain.review.topicReview.Category;
import br.com.senior.burger_place.domain.review.topicReview.TopicReview;

public record ListingTopicReviewDTO(
        Long id,
        int grade,
        Category category

) {

    public ListingTopicReviewDTO(TopicReview topicReview) {
        this(topicReview.getId(), topicReview.getGrade(), topicReview.getCategory());
    }
}
