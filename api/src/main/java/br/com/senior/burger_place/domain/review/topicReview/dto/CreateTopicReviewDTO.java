package br.com.senior.burger_place.domain.review.topicReview.dto;

import br.com.senior.burger_place.domain.review.topicReview.Category;
import jakarta.validation.constraints.NotNull;

public record CreateTopicReviewDTO(
        @NotNull
        Long reviewId,
        @NotNull
        Integer grade,
        @NotNull
        Category category
) {
}
