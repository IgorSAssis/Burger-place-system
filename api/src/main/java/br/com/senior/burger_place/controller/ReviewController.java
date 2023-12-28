package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.review.Review;
import br.com.senior.burger_place.domain.review.ReviewRegisterData;
import br.com.senior.burger_place.domain.review.ReviewService;
import br.com.senior.burger_place.domain.review.ReviewUpdateData;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("reviews")
public class ReviewController {

    @Autowired
    ReviewService service;

    @PostMapping("/{occupationId}")
    @Transactional
    public ResponseEntity<Object> registerReview(
            @PathVariable
            Long occupationId,
            @RequestBody @Valid
            ReviewRegisterData data
    ) {
        Review review = service.addReview(occupationId, data);
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }

    @GetMapping
    public ResponseEntity<Page<Review>> listAllReview(@PageableDefault(size = 10, sort = {"grade"})Pageable pageable){
        Page<Review> reviews = service.listAllReview(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(reviews);
    }

    @GetMapping("/{id}")
    public ResponseEntity listReviewById(@PathVariable Long id){
        Review review = service.listReviewById(id);
        return ResponseEntity.status(HttpStatus.OK).body(review);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity updateReview (
            @PathVariable
            Long id,
            @RequestBody
            @Valid
            ReviewUpdateData data
    ){
        ReviewRegisterData newReview = service.updateReview(id, data);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(newReview);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Object> deleteReview(@PathVariable Long id){
        service.deleteReview(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
