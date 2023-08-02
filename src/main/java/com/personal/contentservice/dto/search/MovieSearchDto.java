package com.personal.contentservice.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieSearchDto extends MediaTypeDto {

  private long id;
  private String title;

  @JsonProperty("media_type")
  private String mediaType;

  @JsonProperty("genre_ids")
  private List<Long> genreIds;

  @JsonProperty("release_date")
  private String date;

  private List<String> genreNames;

  @Override
  public void convertGenreIdsToNames(Map<Long, String> genreMap) {
    if (genreIds != null) {
      genreNames = new ArrayList<>();
      for (long id : genreIds) {
        String name = genreMap.get(id);
        if (name != null) {
          genreNames.add(name);
        }
      }
    }
  }

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Response {
    private long id;
    private String title;
    private String mediaType;
    private List<String> genreNames;
    private String date;

    public static MovieSearchDto.Response from(MovieSearchDto dto) {
      return MovieSearchDto.Response.builder()
          .id(dto.getId())
          .title(dto.getTitle())
          .mediaType(dto.getMediaType())
          .genreNames(dto.getGenreNames())
          .date(dto.getDate())
          .build();
    }
  }

}
