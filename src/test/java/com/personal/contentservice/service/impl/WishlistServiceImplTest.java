package com.personal.contentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.personal.contentservice.domain.Content;
import com.personal.contentservice.domain.ContentKey;
import com.personal.contentservice.domain.User;
import com.personal.contentservice.domain.Wishlist;
import com.personal.contentservice.dto.wishlist.WishlistDto;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.repository.ContentRepository;
import com.personal.contentservice.repository.WishlistRepository;
import com.personal.contentservice.security.principal.PrincipalDetails;
import com.personal.contentservice.service.WishlistService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@SpringBootTest
class WishlistServiceImplTest {

  private WishlistService wishlistService;

  @Mock
  private WishlistRepository wishlistRepository;

  @Mock
  private ContentRepository contentRepository;

  @Mock
  private Authentication authentication;

  private PrincipalDetails principalDetails;
  private User testUser;
  private Content testContent;
  private Wishlist testWishlist;
  private List<Wishlist> testWishlists;

  @BeforeEach
  public void setup() {
    wishlistRepository = mock(WishlistRepository.class);
    contentRepository = mock(ContentRepository.class);
    wishlistService = new WishlistServiceImpl(wishlistRepository, contentRepository);

    testUser = new User();
    testUser.setId(1L);
    testUser.setEmail("test@example.com");

    testContent = new Content();
    testContent.setContentKey(new ContentKey());
    testContent.getContentKey().setIdAndMediaType(1L, "movie");
    testContent.setTitle("Test Content");
    testContent.setContentYear(2023);

    testWishlist = new Wishlist();
    testWishlist.setContent(testContent);
    testWishlist.setUser(testUser);

    testWishlists = new ArrayList<>();
    testWishlists.add(testWishlist);

    principalDetails = new PrincipalDetails(testUser);

    authentication = new UsernamePasswordAuthenticationToken(
            principalDetails,  "", principalDetails.getAuthorities());
  }

  @Test
  @DisplayName("위시리스트_추가_성공")
  public void addToWishlist_ValidContent_Success() {
    WishlistDto.Request request = new WishlistDto.Request();
    request.setId(testContent.getContentKey().getId());
    request.setMediaType(testContent.getContentKey().getMediaType());

    when(contentRepository.findByContentKey_IdAndContentKey_MediaType(
        request.getId(), request.getMediaType())).thenReturn(testContent);

    assertEquals(wishlistService.addToWishlist(authentication, request),
        "위시리스트에 컨텐츠가 추가되었습니다.");
  }

  @Test
  @DisplayName("위시리스트_추가_실패")
  public void addToWishlist_ValidContent_Fail() {
    WishlistDto.Request request = new WishlistDto.Request();
    request.setId(testContent.getContentKey().getId());
    request.setMediaType(testContent.getContentKey().getMediaType());

    assertThrows(CustomException.class, () -> wishlistService.addToWishlist(authentication, request));
  }

  @Test
  @DisplayName("위시리스트_가져오기_성공")
  public void getAllContentsInWishlist_ValidUser_Success() {
    when(wishlistRepository.findByUser(testUser)).thenReturn(testWishlists);

    List<WishlistDto.Response> expectedResponseList = new ArrayList<>();
    WishlistDto.Response expectedResponse = new WishlistDto.Response();
    expectedResponse.setTitle(testContent.getTitle());
    expectedResponse.setContentYear(testContent.getContentYear());
    expectedResponseList.add(expectedResponse);

    List<WishlistDto.Response> result = wishlistService.getAllContentsInWishlist(authentication);

    assertEquals(result, expectedResponseList);
  }

  @Test
  @DisplayName("위시리스트_삭제_성공")
  public void deleteFromWishlist_ValidContent_Success() {
    WishlistDto.Request request = new WishlistDto.Request();
    request.setId(testContent.getContentKey().getId());
    request.setMediaType(testContent.getContentKey().getMediaType());

    when(contentRepository.findByContentKey_IdAndContentKey_MediaType(
        request.getId(), request.getMediaType())).thenReturn(testContent);
    when(wishlistRepository.findByUserAndContent(testUser, testContent))
        .thenReturn(Optional.ofNullable(testWishlist));

    assertEquals(wishlistService.deleteFromWishlist(authentication, request),
        "위시리스트에서 컨텐츠가 삭제되었습니다.");
  }

  @Test
  @DisplayName("위시리스트_삭제_실패")
  public void deleteFromWishlist_InvalidContent_ThrowsException() {
    WishlistDto.Request request = new WishlistDto.Request();
    request.setId(1L);
    request.setMediaType("movie");

    assertThrows(CustomException.class, () -> wishlistService.deleteFromWishlist(authentication, request));
  }

}