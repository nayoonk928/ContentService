package com.personal.contentservice.service.impl;

import static com.personal.contentservice.exception.ErrorCode.ALREADY_REPORTED_REVIEW;
import static com.personal.contentservice.exception.ErrorCode.REVIEW_ALREADY_EXISTS;
import static com.personal.contentservice.exception.ErrorCode.REVIEW_NOT_FOUND;
import static com.personal.contentservice.type.ReactionType.LIKE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.personal.contentservice.domain.Content;
import com.personal.contentservice.domain.ContentKey;
import com.personal.contentservice.domain.Review;
import com.personal.contentservice.domain.User;
import com.personal.contentservice.dto.review.ReviewAddDto;
import com.personal.contentservice.dto.review.ReviewDeleteDto;
import com.personal.contentservice.dto.review.ReviewReactionDto;
import com.personal.contentservice.dto.review.ReviewReportDto;
import com.personal.contentservice.dto.review.ReviewUpdateDto;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.lock.LockService;
import com.personal.contentservice.repository.ContentRepository;
import com.personal.contentservice.repository.ReviewReactionRepository;
import com.personal.contentservice.repository.ReviewReportRepository;
import com.personal.contentservice.repository.ReviewRepository;
import com.personal.contentservice.security.principal.PrincipalDetails;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

class ReviewServiceImplTest {

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private ReviewReactionRepository reviewReactionRepository;

  @Mock
  private ReviewReportRepository reviewReportRepository;

  @InjectMocks
  private ReviewServiceImpl reviewService;

  @Mock
  private Authentication authentication;

  @Mock
  private LockService lockService;

  private PrincipalDetails principalDetails;
  private User testUser;
  private User testUser2;
  private Content testContent;
  private Review testReview;

  @BeforeEach
  void setUp() {
    reviewRepository = mock(ReviewRepository.class);
    contentRepository = mock(ContentRepository.class);
    reviewReactionRepository = mock(ReviewReactionRepository.class);
    reviewReportRepository = mock(ReviewReportRepository.class);
    lockService = mock(LockService.class);
    reviewService = new ReviewServiceImpl(
        contentRepository, reviewRepository, reviewReactionRepository,
        reviewReportRepository, lockService);

    testUser = new User();
    testUser.setId(1L);
    testUser.setEmail("test@example.com");

    testUser2 = new User();
    testUser2.setId(2L);
    testUser2.setEmail("test2@example.com");

    testContent = new Content();
    testContent.setContentKey(new ContentKey());
    testContent.getContentKey().setIdAndMediaType(1L, "movie");
    testContent.setTitle("Test Content");
    testContent.setContentYear(2023);

    testReview = new Review();
    testReview.setId(1L);
    testReview.setComment("재밌어요");
    testReview.setRating(5.0);
    testReview.setUser(testUser2);

    principalDetails = new PrincipalDetails(testUser);

    authentication = new UsernamePasswordAuthenticationToken(
        principalDetails, "", principalDetails.getAuthorities());
  }

  @Test
  @DisplayName("리뷰작성_성공")
  void testAddReview_Success() {
    //given
    ReviewAddDto request = ReviewAddDto.builder()
        .contentKey(testContent.getContentKey())
        .comment("재밌어요!")
        .rating(5.0)
        .build();

    //when
    when(contentRepository.findByContentKey_IdAndContentKey_MediaType(
        request.getContentKey().getId(), request.getContentKey().getMediaType()))
        .thenReturn(testContent);

    //then
    assertEquals(reviewService.addReview(authentication, request),
        "컨텐츠에 리뷰가 등록되었습니다.");
  }

  @Test
  @DisplayName("리뷰작성_실패_이미작성된리뷰있음")
  void testAddReview_Fail() {
    //given
    ReviewAddDto request = ReviewAddDto.builder()
        .contentKey(testContent.getContentKey())
        .comment("재밌어요!")
        .rating(5.0)
        .build();

    //when
    when(contentRepository.findByContentKey_IdAndContentKey_MediaType(
        request.getContentKey().getId(), request.getContentKey().getMediaType()))
        .thenReturn(testContent);
    when(reviewRepository.existsByUserAndContent(testUser, testContent))
        .thenReturn(true);

    //then
    CustomException exception = assertThrows(CustomException.class,
        () -> reviewService.addReview(authentication, request));
    assertEquals(REVIEW_ALREADY_EXISTS, exception.getErrorCode());
  }

  @Test
  @DisplayName("리뷰수정_성공")
  void testUpdateReview_Success() {
    //given
    Review prevReview = Review.builder()
        .user(testUser)
        .content(testContent)
        .comment("재밌어요!")
        .rating(5.0)
        .build();

    ReviewUpdateDto request = ReviewUpdateDto.builder()
        .contentKey(testContent.getContentKey())
        .rating(4.5)
        .build();

    //when
    when(contentRepository.findByContentKey_IdAndContentKey_MediaType(
        request.getContentKey().getId(), request.getContentKey().getMediaType()))
        .thenReturn(testContent);
    when(reviewRepository.findByUserAndContent(testUser, testContent))
        .thenReturn(Optional.ofNullable(prevReview));

    //then
    assertEquals(reviewService.updateReview(authentication, request),
        "리뷰가 수정되었습니다.");
  }

  @Test
  @DisplayName("리뷰수정_실패_리뷰없음")
  void testUpdateReview_Fail() {
    //given
    Review prevReview = Review.builder()
        .user(testUser)
        .content(testContent)
        .comment("재밌어요!")
        .rating(5.0)
        .build();

    ReviewUpdateDto request = ReviewUpdateDto.builder()
        .contentKey(testContent.getContentKey())
        .rating(4.5)
        .build();

    //when
    when(contentRepository.findByContentKey_IdAndContentKey_MediaType(
        request.getContentKey().getId(), request.getContentKey().getMediaType()))
        .thenReturn(testContent);
    when(reviewRepository.findByUserAndContent(testUser, testContent))
        .thenThrow(new CustomException(REVIEW_NOT_FOUND));

    //then
    CustomException exception = assertThrows(CustomException.class,
        () -> reviewService.updateReview(authentication, request));
    assertEquals(REVIEW_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("리뷰삭제_성공")
  void testDeleteReview_Success() {
    //given
    Review prevReview = Review.builder()
        .user(testUser)
        .content(testContent)
        .comment("재밌어요!")
        .rating(5.0)
        .build();

    ReviewDeleteDto request = ReviewDeleteDto.builder()
        .contentKey(testContent.getContentKey())
        .build();

    //when
    when(contentRepository.findByContentKey_IdAndContentKey_MediaType(
        request.getContentKey().getId(), request.getContentKey().getMediaType()))
        .thenReturn(testContent);
    when(reviewRepository.findByUserAndContent(testUser, testContent))
        .thenReturn(Optional.ofNullable(prevReview));

    //then
    assertEquals(reviewService.deleteReview(authentication, request),
        "리뷰가 삭제되었습니다.");
  }

  @Test
  @DisplayName("리뷰삭제_실패_리뷰없음")
  void testDeleteReview_Fail() {
    //given
    ReviewDeleteDto request = ReviewDeleteDto.builder()
        .contentKey(testContent.getContentKey())
        .build();

    //when
    when(contentRepository.findByContentKey_IdAndContentKey_MediaType(
        request.getContentKey().getId(), request.getContentKey().getMediaType()))
        .thenReturn(testContent);
    when(reviewRepository.findByUserAndContent(testUser, testContent))
        .thenThrow(new CustomException(REVIEW_NOT_FOUND));

    //then
    CustomException exception = assertThrows(CustomException.class,
        () -> reviewService.deleteReview(authentication, request));
    assertEquals(REVIEW_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("리뷰반응_성공")
  void testReactReview_Success() {
    //given
    ReviewReactionDto request = ReviewReactionDto.builder()
        .reviewId(1L)
        .reactionType(LIKE)
        .build();

    principalDetails = new PrincipalDetails(testUser2);

    authentication = new UsernamePasswordAuthenticationToken(
        principalDetails, "", principalDetails.getAuthorities());

    //when
    when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.ofNullable(testReview));
    when(reviewReactionRepository.findByUserAndReview(testUser2, testReview))
        .thenReturn(null);

    //then
    assertEquals(reviewService.reactReview(authentication, request),
        "리뷰를 추천했습니다.");
  }

  @Test
  @DisplayName("리뷰신고_성공")
  void testReportReview_Success() {
    //given
    ReviewReportDto request = ReviewReportDto.builder()
        .reviewId(1L)
        .reason("욕설 사용")
        .build();

    principalDetails = new PrincipalDetails(testUser2);

    authentication = new UsernamePasswordAuthenticationToken(
        principalDetails, "", principalDetails.getAuthorities());

    //when
    when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.ofNullable(testReview));
    when(reviewReportRepository.existsByUserAndReview(testUser2, testReview))
        .thenReturn(false);

    //then
    assertEquals(reviewService.reportReview(authentication, request),
        "리뷰를 신고했습니다.");
  }

  @Test
  @DisplayName("리뷰신고_실패_이미신고한리뷰")
  void testReportReview_Fail() {
    //given
    ReviewReportDto request = ReviewReportDto.builder()
        .reviewId(1L)
        .reason("욕설 사용")
        .build();

    principalDetails = new PrincipalDetails(testUser2);

    authentication = new UsernamePasswordAuthenticationToken(
        principalDetails, "", principalDetails.getAuthorities());

    //when
    when(reviewRepository.findById(testReview.getId())).thenReturn(Optional.ofNullable(testReview));
    when(reviewReportRepository.existsByUserAndReview(testUser2, testReview))
        .thenThrow(new CustomException(ALREADY_REPORTED_REVIEW));

    //then
    CustomException exception = assertThrows(CustomException.class,
        () -> reviewService.reportReview(authentication, request));
    assertEquals(ALREADY_REPORTED_REVIEW, exception.getErrorCode());
  }

}