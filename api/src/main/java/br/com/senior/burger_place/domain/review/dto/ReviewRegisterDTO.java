package br.com.senior.burger_place.domain.review.dto;

import br.com.senior.burger_place.domain.review.topicReview.dto.TopicReviewRegisterDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record ReviewRegisterDTO(
        @NotNull
        UUID occupationId,
        String comment,
        @NotEmpty
        List<TopicReviewRegisterDTO> items
) {
}
