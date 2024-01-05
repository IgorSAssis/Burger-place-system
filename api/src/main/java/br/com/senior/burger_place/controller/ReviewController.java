package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.review.Review;
import br.com.senior.burger_place.domain.review.ReviewService;
import br.com.senior.burger_place.domain.review.dto.ListingReviewDTO;
import br.com.senior.burger_place.domain.review.dto.ReviewRegisterDTO;
import br.com.senior.burger_place.domain.review.dto.ReviewUpdateDTO;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("reviews")
public class ReviewController {

    @Autowired
    ReviewService service;

    @PostMapping
    @Transactional
    public ResponseEntity<Object> register(
            @RequestBody @Valid
            ReviewRegisterDTO dto,
            UriComponentsBuilder uriBuilder
    ) {
        ListingReviewDTO reviews = service.addReview(dto);
        var uri = uriBuilder.path("/reviews/{id}").buildAndExpand(reviews.id()).toUri();
        return ResponseEntity.created(uri).body(reviews);
    }


    @GetMapping
    public ResponseEntity<Page<ListingReviewDTO>> listAllReview(@PageableDefault(size = 10, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Review> reviews = service.listAllReview(pageable);
        Page<ListingReviewDTO> response = reviews.map(ListingReviewDTO::new);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity listReviewById(@PathVariable Long id) {
        ListingReviewDTO review = service.listReviewById(id);
        return ResponseEntity.status(HttpStatus.OK).body(review);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity updateReview(
            @PathVariable
            Long id,
            @RequestBody
            @Valid
            ReviewUpdateDTO dto
    ) {
        ReviewRegisterDTO newReview = service.updateReview(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(newReview);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Object> deleteReview(@PathVariable Long id) {
        service.deleteReview(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
