package com.personal.contentservice.dto.search;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchContentDto {

  private long id;
  private String title;
  private String mediaType;
  private List<String> genreNames;
  private String date;
  private double averageRating;

}
