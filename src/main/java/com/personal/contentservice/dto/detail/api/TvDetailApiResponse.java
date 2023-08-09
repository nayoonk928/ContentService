package com.personal.contentservice.dto.detail.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.personal.contentservice.domain.Genre;
import com.personal.contentservice.dto.detail.CreditsDto;
import com.personal.contentservice.dto.detail.ProductionCountryDto;
import com.personal.contentservice.dto.detail.TvSeasonDto;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TvDetailApiResponse {

  private String mediaType;
  private String name;
  private String originalName;
  private String originalLanguage;
  private List<Genre> genres;
  private String overview;
  private String firstAirDate;
  private String lastAirDate;
  private int numberOfEpisodes;
  private int numberOfSeasons;
  private List<String> originCountry;
  private List<ProductionCountryDto> productionCountries;
  private List<TvSeasonDto> seasons;
  private CreditsDto credits;

}
