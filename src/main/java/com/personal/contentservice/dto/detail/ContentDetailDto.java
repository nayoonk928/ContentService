package com.personal.contentservice.dto.detail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContentDetailDto {

  @JsonIgnore
  private String title;

  @JsonIgnore
  private GenreDto genres;

  @JsonIgnore
  private int contentYear;

  private String originalTitle;
  private String originalLanguage;
  private String overview;
  private List<String> productionCountries;
  private List<String> actorsInfo;
  private List<String> directorsName;

  // movie
  private String tagline;
  private String releaseDate;
  private Integer runtime;

  // tv
  private String firstAirDate;
  private String lastAirDate;
  private Integer numberOfEpisodes;
  private Integer numberOfSeasons;
  private List<TvSeasonDto> seasons;

  @JsonCreator
  public ContentDetailDto(String value) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    ContentDetailDto root = objectMapper.readValue(value, ContentDetailDto.class);

    this.originalTitle = root.getOriginalTitle();
    this.originalLanguage = root.getOriginalLanguage();
    this.overview = root.getOverview();
    this.productionCountries = root.getProductionCountries();
    this.actorsInfo = root.getActorsInfo();
    this.directorsName = root.getDirectorsName();
    this.tagline = root.getTagline();
    this.releaseDate = root.getReleaseDate();
    this.runtime = root.getRuntime();
    this.firstAirDate = root.getFirstAirDate();
    this.lastAirDate = root.getLastAirDate();
    this.numberOfEpisodes = root.getNumberOfEpisodes();
    this.numberOfSeasons = root.getNumberOfSeasons();
    this.seasons = root.getSeasons();
  }

}