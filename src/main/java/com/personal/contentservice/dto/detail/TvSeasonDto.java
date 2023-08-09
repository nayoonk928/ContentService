package com.personal.contentservice.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TvSeasonDto {

  private String airDate;
  private int episodeCount;
  private long id;
  private String name;
  private String overview;
  private int seasonNumber;

}
