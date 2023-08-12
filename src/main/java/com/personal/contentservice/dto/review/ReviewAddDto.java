package com.personal.contentservice.dto.review;

import com.personal.contentservice.domain.ContentKey;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ReviewAddDto {

  @NotNull
  private ContentKey contentKey;

  @NotNull
  private String comment;

  @NotNull
  private double rating;

}
