package com.personal.contentservice.dto.detail.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.personal.contentservice.domain.Genre;
import com.personal.contentservice.dto.detail.CreditsDto;
import com.personal.contentservice.dto.detail.ProductionCountryDto;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDetailApiResponse {

  private String mediaType;
  private String title;
  private String originalTitle;
  private String originalLanguage;
  private List<Genre> genres;
  private String tagline;
  private String overview;
  private List<ProductionCountryDto> productionCountries;
  private String releaseDate;
  private int runtime;
  private CreditsDto credits;

}
