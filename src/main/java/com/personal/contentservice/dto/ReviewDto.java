package com.personal.contentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {

  private long id;
  private UserDto user;
  private String comment;
  private double rating;
  private long likeCount;
  private long dislikeCount;

}

