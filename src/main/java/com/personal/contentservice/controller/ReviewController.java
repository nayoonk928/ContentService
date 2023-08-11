package com.personal.contentservice.controller;

import com.personal.contentservice.dto.review.ReviewAddDto;
import com.personal.contentservice.dto.review.ReviewDeleteDto;
import com.personal.contentservice.dto.review.ReviewUpdateDto;
import com.personal.contentservice.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

  private final ReviewService reviewService;

  @PostMapping
  public ResponseEntity<String> addReview(
      Authentication authentication,
      @Valid @RequestBody ReviewAddDto request
  ) {
    return ResponseEntity.ok().body(reviewService.addReview(authentication, request));
  }

  @PutMapping
  public ResponseEntity<String> updateReview(
      Authentication authentication,
      @Valid @RequestBody ReviewUpdateDto request
  ) {
    return ResponseEntity.ok().body(reviewService.updateReview(authentication, request));
  }

  @DeleteMapping
  public ResponseEntity<String> deleteReview(
      Authentication authentication,
      @Valid @RequestBody ReviewDeleteDto request
  ) {
    return ResponseEntity.ok().body(reviewService.deleteReview(authentication, request));
  }

}
