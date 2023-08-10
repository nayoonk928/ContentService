package com.personal.contentservice.service.impl;

import static com.personal.contentservice.exception.ErrorCode.CONTENT_NOT_FOUND;
import static com.personal.contentservice.exception.ErrorCode.REVIEW_ALREADY_EXISTS;
import static com.personal.contentservice.exception.ErrorCode.REVIEW_NOT_FOUND;

import com.personal.contentservice.domain.Content;
import com.personal.contentservice.domain.ContentKey;
import com.personal.contentservice.domain.Review;
import com.personal.contentservice.domain.User;
import com.personal.contentservice.dto.review.ReviewAddDto;
import com.personal.contentservice.dto.review.ReviewDeleteDto;
import com.personal.contentservice.dto.review.ReviewUpdateDto;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.repository.ContentRepository;
import com.personal.contentservice.repository.ReviewRepository;
import com.personal.contentservice.service.ReviewService;
import com.personal.contentservice.util.UserAuthenticationUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final ContentRepository contentRepository;
  private final ReviewRepository reviewRepository;

  @Override
  public String addReview(Authentication authentication, ReviewAddDto request) {
    User user = UserAuthenticationUtils.getUser(authentication);
    Content content = getContentByIdAndMediaType(request.getContentKey());

    if (reviewRepository.existsByUserAndContent(user, content)) {
      throw new CustomException(REVIEW_ALREADY_EXISTS);
    }

    Review review = Review.builder()
        .user(user)
        .content(content)
        .comment(request.getComment())
        .rating(request.getRating())
        .build();

    reviewRepository.save(review);
    calculateAverageRating(content);
    return "컨텐츠에 리뷰가 등록되었습니다.";
  }

  @Override
  public String updateReview(Authentication authentication, ReviewUpdateDto request) {
    User user = UserAuthenticationUtils.getUser(authentication);
    Content content = getContentByIdAndMediaType(request.getContentKey());

    Review review = reviewRepository.findByUserAndContent(user, content)
        .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

    if (request.getComment() != null) {
      review.setComment(request.getComment());
    }

    if (request.getRating() != null) {
      review.setRating(request.getRating());
    }

    reviewRepository.save(review);
    calculateAverageRating(content);
    return "리뷰가 수정되었습니다.";
  }

  @Override
  public String deleteReview(Authentication authentication, ReviewDeleteDto request) {
    User user = UserAuthenticationUtils.getUser(authentication);
    Content content = getContentByIdAndMediaType(request.getContentKey());

    Review review = reviewRepository.findByUserAndContent(user, content)
        .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

    reviewRepository.delete(review);
    calculateAverageRating(content);
    return "리뷰가 삭제되었습니다.";
  }

  private Content getContentByIdAndMediaType(ContentKey contentKey) {
    long contentId = contentKey.getId();
    String mediaType = contentKey.getMediaType();
    Content content =
        contentRepository.findByContentKey_IdAndContentKey_MediaType(contentId, mediaType);

    if (content == null) {
      throw new CustomException(CONTENT_NOT_FOUND);
    }

    return content;
  }

  private void calculateAverageRating(Content content) {
    List<Review> reviews = reviewRepository.findAllByContent(content);

    if (reviews.isEmpty()) {
      content.setAverageRating(0.0);
    } else {
      double totalRating = 0.0;
      for (Review review : reviews) {
        totalRating += review.getRating();
      }
      double averageRating = totalRating / reviews.size();
      content.setAverageRating(averageRating);
      contentRepository.save(content);
    }
  }

}