package com.personal.contentservice.service;

import com.personal.contentservice.dto.review.ReviewAddDto;
import com.personal.contentservice.dto.review.ReviewDeleteDto;
import com.personal.contentservice.dto.review.ReviewUpdateDto;
import org.springframework.security.core.Authentication;

public interface ReviewService {

  String addReview(Authentication authentication, ReviewAddDto request);

  String updateReview(Authentication authentication, ReviewUpdateDto request);

  String deleteReview(Authentication authentication, ReviewDeleteDto request);

}
