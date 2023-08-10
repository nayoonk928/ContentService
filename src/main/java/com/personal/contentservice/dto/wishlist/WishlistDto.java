package com.personal.contentservice.dto.wishlist;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class WishlistDto {

  @Getter
  @Setter
  public static class Request {
    private long id;
    private String mediaType;
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Response {

    private String title;

    private int contentYear;

    private double averageRating;
  }

}
