package com.personal.contentservice.dto.review;

import com.personal.contentservice.type.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReactionDto {

  @NotNull
  private long reviewId;

  @NotNull
  private ReactionType reactionType;

}
