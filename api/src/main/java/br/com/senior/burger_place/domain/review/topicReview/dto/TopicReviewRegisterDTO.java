package br.com.senior.burger_place.domain.review.topicReview.dto;

import br.com.senior.burger_place.domain.review.topicReview.Category;
import br.com.senior.burger_place.domain.review.topicReview.TopicReview;
import jakarta.validation.constraints.NotNull;

public record TopicReviewRegisterDTO(
        @NotNull
        Integer grade,
        @NotNull
        Category category
) {
    public TopicReviewRegisterDTO(TopicReview topicReview) {
        this(topicReview.getGrade(), topicReview.getCategory());
    }
}
