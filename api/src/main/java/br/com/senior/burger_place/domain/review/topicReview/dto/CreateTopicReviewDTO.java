package br.com.senior.burger_place.domain.review.topicReview.dto;

import br.com.senior.burger_place.domain.review.topicReview.Category;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateTopicReviewDTO(
        @NotNull
        UUID reviewId,
        @NotNull
        Integer grade,
        @NotNull
        Category category
) {
}
