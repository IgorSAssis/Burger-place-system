package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.review.topicReview.*;
import br.com.senior.burger_place.domain.review.topicReview.dto.CreateTopicReviewDTO;
import br.com.senior.burger_place.domain.review.topicReview.dto.ListingTopicReviewDTO;
import br.com.senior.burger_place.domain.review.topicReview.dto.TopicReviewRegisterDTO;
import br.com.senior.burger_place.domain.review.topicReview.dto.TopicReviewUpdateDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("reviews/topics")
public class TopicReviewsController {

    @Autowired
    TopicReviewService service;

    @GetMapping()
    public ResponseEntity<Page<ListingTopicReviewDTO>> listAllTByCategory(
            @RequestParam(required = false) String category,
            Pageable pageable
    ) {
        try {
            Category categoryUpperCase = Category.valueOf(category.toUpperCase());
            Page<TopicReview> topicReviews = service.listTopicReviewByCategory(categoryUpperCase, pageable);
            return ResponseEntity.ok().body(topicReviews.map(ListingTopicReviewDTO::new));
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Não há avaliação para a categoria: " + category);
        }
    }

    @PostMapping
    @Transactional
    public ResponseEntity<ListingTopicReviewDTO> register(
            @Valid
            @RequestBody
            CreateTopicReviewDTO dto,
            UriComponentsBuilder uriBuilder
    ) {
        ListingTopicReviewDTO createdTopicReview = service.register(dto);
        var uri = uriBuilder.path("/reviews/{id}").buildAndExpand(createdTopicReview.id()).toUri();
        return ResponseEntity.created(uri).body(createdTopicReview);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<Object> update(
            @PathVariable
            Long id,
            @RequestBody
            TopicReviewUpdateDTO dto
    ) {
        TopicReviewRegisterDTO newTopicReview = service.updateTopicReview(id, dto);
        return ResponseEntity.ok().body(newTopicReview);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        service.deleteTopicReview(id);
        return ResponseEntity.noContent().build();
    }
}
