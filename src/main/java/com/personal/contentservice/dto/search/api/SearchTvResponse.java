package com.personal.contentservice.dto.search.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.personal.contentservice.dto.search.SearchContentDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchTvResponse extends MediaTypeDto {

  private long id;

  private String name;

  private String mediaType;

  private List<Long> genreIds;

  private String firstAirDate;

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

  @Override
  public SearchContentDto toSearchContentDto() {
    return SearchContentDto.builder()
        .id(id)
        .title(name)
        .mediaType(mediaType)
        .genreNames(genreNames)
        .date(firstAirDate)
        .build();
  }

}
