package com.personal.contentservice.service.impl;

import static com.personal.contentservice.exception.ErrorCode.CONTENT_NOT_FOUND;
import static com.personal.contentservice.exception.ErrorCode.CONTENT_NOT_IN_WISHLIST;
import static com.personal.contentservice.exception.ErrorCode.USER_NOT_FOUND;

import com.personal.contentservice.domain.Content;
import com.personal.contentservice.domain.User;
import com.personal.contentservice.domain.Wishlist;
import com.personal.contentservice.dto.wishlist.WishlistDto;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.repository.ContentRepository;
import com.personal.contentservice.repository.WishlistRepository;
import com.personal.contentservice.security.principal.PrincipalDetails;
import com.personal.contentservice.service.WishlistService;
import com.personal.contentservice.util.UserAuthenticationUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WishlistServiceImpl implements WishlistService {

  private final WishlistRepository wishlistRepository;
  private final ContentRepository contentRepository;

  @Override
  public String addToWishlist(Authentication authentication, WishlistDto.Request request) {
    User user = UserAuthenticationUtils.getUser(authentication);

    Content content = contentRepository.findByContentKey_IdAndContentKey_MediaType(
        request.getId(), request.getMediaType());

    if (content == null) {
      throw new CustomException(CONTENT_NOT_FOUND);
    }

    Wishlist wishlistItem = new Wishlist();
    wishlistItem.setUser(user);
    wishlistItem.setContent(content);

    wishlistRepository.save(wishlistItem);

    return "위시리스트에 컨텐츠가 추가되었습니다.";
  }

  @Override
  public List<WishlistDto.Response> getAllContentsInWishlist(Authentication authentication) {
    User user = UserAuthenticationUtils.getUser(authentication);
    List<Wishlist> wishlists = wishlistRepository.findByUser(user);

    List<WishlistDto.Response> wishlistDto = wishlists.stream()
        .map(wishlist -> convertContentToDto(wishlist.getContent()))
        .collect(Collectors.toList());

    return wishlistDto;
  }

  @Override
  public String deleteFromWishlist(Authentication authentication, WishlistDto.Request request) {
    User user = UserAuthenticationUtils.getUser(authentication);

    Content content = contentRepository
        .findByContentKey_IdAndContentKey_MediaType(request.getId(), request.getMediaType());

    if (content == null) {
      throw new CustomException(CONTENT_NOT_FOUND);
    }

    Wishlist wishlist = wishlistRepository.findByUserAndContent(user, content)
            .orElseThrow(() -> new CustomException(CONTENT_NOT_IN_WISHLIST));

    wishlistRepository.delete(wishlist);

    return "위시리스트에서 컨텐츠가 삭제되었습니다.";
  }

  private WishlistDto.Response convertContentToDto(Content content) {
    return WishlistDto.Response.builder()
        .title(content.getTitle())
        .contentYear(content.getContentYear())
        .averageRating(content.getAverageRating())
        .build();
  }

}
