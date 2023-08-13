package com.personal.contentservice.dto;

import com.personal.contentservice.dto.detail.ContentDetailDto;
import com.personal.contentservice.dto.detail.GenreDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentDto {

  private long id;
  private String mediaType;
  private String title;
  private int contentYear;
  private GenreDto genres;
  private double averageRating;
  private ContentDetailDto details;
  private List<ReviewDto> reviews;

}
