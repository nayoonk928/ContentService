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
public class ReviewDeleteDto {

  @NotNull
  private ContentKey contentKey;

}
