package com.personal.contentservice.dto.detail;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TvDetailDto extends ContentDetailDto {

  private String originalTitle;
  private String originalLanguage;
  private String overview;
  private List<String> productionCountries;
  private List<String> actorsInfo;
  private List<String> directorsName;

  private String firstAirDate;
  private String lastAirDate;
  private int numberOfEpisodes;
  private int numberOfSeasons;
  private List<TvSeasonDto> seasons;

}
