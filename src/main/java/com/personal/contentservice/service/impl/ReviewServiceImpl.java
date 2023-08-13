package com.personal.contentservice.service.impl;

import static com.personal.contentservice.exception.ErrorCode.ALREADY_REPORTED_REVIEW;
import static com.personal.contentservice.exception.ErrorCode.CONTENT_NOT_FOUND;
import static com.personal.contentservice.exception.ErrorCode.REVIEW_ALREADY_EXISTS;
import static com.personal.contentservice.exception.ErrorCode.REVIEW_NOT_FOUND;

import com.personal.contentservice.domain.Content;
import com.personal.contentservice.domain.ContentKey;
import com.personal.contentservice.domain.Review;
import com.personal.contentservice.domain.ReviewReaction;
import com.personal.contentservice.domain.ReviewReport;
import com.personal.contentservice.domain.User;
import com.personal.contentservice.dto.review.ReviewReactionDto;
import com.personal.contentservice.dto.review.ReviewReportDto;
import com.personal.contentservice.dto.review.ReviewAddDto;
import com.personal.contentservice.dto.review.ReviewDeleteDto;
import com.personal.contentservice.dto.review.ReviewUpdateDto;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.lock.LockService;
import com.personal.contentservice.repository.ContentRepository;
import com.personal.contentservice.repository.ReviewReactionRepository;
import com.personal.contentservice.repository.ReviewReportRepository;
import com.personal.contentservice.repository.ReviewRepository;
import com.personal.contentservice.service.ReviewService;
import com.personal.contentservice.type.ReactionType;
import com.personal.contentservice.util.UserAuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

  private final ContentRepository contentRepository;
  private final ReviewRepository reviewRepository;
  private final ReviewReactionRepository reviewReactionRepository;
  private final ReviewReportRepository reviewReportRepository;
  private final LockService lockService;

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
    saveCalculateAverageRating(content);
    return "컨텐츠에 리뷰가 등록되었습니다.";
  }

  @Override
  public String updateReview(Authentication authentication, ReviewUpdateDto request) {
    User user = UserAuthenticationUtils.getUser(authentication);
    Content content = getContentByIdAndMediaType(request.getContentKey());

    Review review = reviewRepository.findByUserAndContent(user, content)
        .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

    boolean isUpdated = false;

    if (!request.getComment().equals(review.getComment())) {
      review.setComment(request.getComment());
      isUpdated = true;
    }

    if (!request.getRating().equals(review.getRating())) {
      review.setRating(request.getRating());
      isUpdated = true;
    }

    if (isUpdated) {
      reviewRepository.save(review);
      saveCalculateAverageRating(content);
      return "리뷰가 수정되었습니다.";
    } else {
      return "변경된 내용이 없어 리뷰를 수정하지 않았습니다.";
    }
  }

  @Override
  public String deleteReview(Authentication authentication, ReviewDeleteDto request) {
    User user = UserAuthenticationUtils.getUser(authentication);
    Content content = getContentByIdAndMediaType(request.getContentKey());

    Review review = reviewRepository.findByUserAndContent(user, content)
        .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

    String lockKey = "review_lock:" + review.getId();

    try {
      lockService.lock(lockKey);

      reviewRepository.delete(review);
      saveCalculateAverageRating(content);
      return "리뷰가 삭제되었습니다.";
    } finally {
      lockService.unlock(lockKey);
    }
  }

  @Override
  public String reactReview(Authentication authentication, ReviewReactionDto request) {
    User user = UserAuthenticationUtils.getUser(authentication);
    Review review = reviewRepository.findById(request.getReviewId())
        .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

    ReviewReaction reaction = reviewReactionRepository.findByUserAndReview(user, review);
    ReactionType reactionType = request.getReactionType();

    if (reaction != null) {
      saveDecrementReactionCount(review, reaction.getReactionType());
      reviewReactionRepository.delete(reaction);

      if (!reaction.getReactionType().equals(reactionType)) {
        saveIncrementReactionCount(review, reactionType);
        saveReviewReaction(user, review, reactionType);
      }
      return getReactionMessage(reaction.getReactionType(), reactionType, true);
    }

    saveIncrementReactionCount(review, reactionType);
    saveReviewReaction(user, review, reactionType);

    return getReactionMessage(reactionType, reactionType, false);
  }

  @Override
  public String reportReview(Authentication authentication, ReviewReportDto request) {
    User user = UserAuthenticationUtils.getUser(authentication);
    Review review = reviewRepository.findById(request.getReviewId())
        .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

    if (reviewReportRepository.existsByUserAndReview(user, review)) {
      throw new CustomException(ALREADY_REPORTED_REVIEW);
    }

    ReviewReport report = ReviewReport.builder()
        .user(user)
        .review(review)
        .reason(request.getReason())
        .build();

    reviewReportRepository.save(report);
    return "리뷰를 신고했습니다.";
  }

  private void saveIncrementReactionCount(Review review, ReactionType reactionType) {
    if (reactionType.equals(ReactionType.LIKE)) {
      review.setLikeCount(review.getLikeCount() + 1);
      reviewRepository.save(review);
    } else {
      review.setDislikeCount(review.getDislikeCount() + 1);
      reviewRepository.save(review);
    }
  }

  private void saveDecrementReactionCount(Review review, ReactionType reactionType) {
    if (reactionType.equals(ReactionType.LIKE)) {
      review.setLikeCount(review.getLikeCount() - 1);
      reviewRepository.save(review);
    } else {
      review.setDislikeCount(review.getDislikeCount() - 1);
      reviewRepository.save(review);
    }
  }

  private void saveReviewReaction(User user, Review review, ReactionType reactionType) {
    ReviewReaction reaction = ReviewReaction.builder()
        .user(user)
        .review(review)
        .reactionType(reactionType)
        .build();
    reviewReactionRepository.save(reaction);
  }

  private String getReactionMessage(
      ReactionType previousType, ReactionType currentType, boolean isCancel
  ) {
    if (isCancel && previousType.equals(currentType)) {
      String typeName = previousType.equals(ReactionType.LIKE) ? "추천" : "비추천";
      return String.format("리뷰 %s을 취소했습니다.", typeName);
    } else {
      String typeName = currentType.equals(ReactionType.LIKE) ? "추천" : "비추천";
      return String.format("리뷰를 %s했습니다.", typeName);
    }
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


  private void saveCalculateAverageRating(Content content) {
    Double averageRating = reviewRepository.calculateAverageRatingByContent(content);

    if (averageRating == null) {
      averageRating = 0.0;
    }

    content.setAverageRating(averageRating);
    contentRepository.save(content);
  }

}