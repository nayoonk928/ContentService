package com.personal.contentservice.dto.search.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchPersonResponse extends MediaTypeDto {

  private long id;
  private String name;

  private String mediaType;

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.EXISTING_PROPERTY,
      property = "media_type", visible = true)
  @JsonSubTypes({
      @JsonSubTypes.Type(value = SearchMovieResponse.class, name = "movie"),
      @JsonSubTypes.Type(value = SearchTvResponse.class, name = "tv")
  })
  private List<MediaTypeDto> knownFor;

}
