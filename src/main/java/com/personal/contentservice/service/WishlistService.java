package com.personal.contentservice.service;

import com.personal.contentservice.dto.wishlist.WishlistDto;
import java.util.List;
import org.springframework.security.core.Authentication;

public interface WishlistService {

  String addToWishlist(Authentication authentication, WishlistDto.Request request);

  List<WishlistDto.Response> getAllContentsInWishlist(Authentication authentication);

  String deleteFromWishlist(Authentication authentication, WishlistDto.Request request);

}
