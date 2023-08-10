package com.personal.contentservice.controller;

import com.personal.contentservice.dto.ContentDto;
import com.personal.contentservice.dto.wishlist.WishlistDto;
import com.personal.contentservice.service.WishlistService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

  private final WishlistService wishlistService;

  // wishlist 에 컨텐츠 담기
  @PostMapping
  public ResponseEntity<String> addToWishlist(
      Authentication authentication,
      @Valid @RequestBody WishlistDto.Request request
  ) {
    return ResponseEntity.ok().body(wishlistService.addToWishlist(authentication, request));
  }

  // wishlist 에 담은 컨텐츠 모두 보기
  @GetMapping
  public ResponseEntity<List<WishlistDto.Response>> getAllContentsInWishlist(
      Authentication authentication
  ) {
    return ResponseEntity.ok().body(wishlistService.getAllContentsInWishlist(authentication));
  }

  // wishlist 에서 컨텐츠 삭제
  @DeleteMapping
  public ResponseEntity<String> deleteFromWishlist(
      Authentication authentication,
      @Valid @RequestBody WishlistDto.Request request
  ) {
    return ResponseEntity.ok().body(wishlistService.deleteFromWishlist(authentication, request));
  }

}
