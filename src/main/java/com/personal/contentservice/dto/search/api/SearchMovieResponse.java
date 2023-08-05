package com.personal.contentservice.dto.search.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchMovieResponse extends MediaTypeDto {

  private long id;
  private String title;
  private String mediaType;
  private List<Long> genreIds;
  private String releaseDate;
  private List<String> genreNames;

}
