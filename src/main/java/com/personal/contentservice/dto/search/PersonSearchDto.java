package com.personal.contentservice.dto.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonSearchDto extends MediaTypeDto {

  private long id;
  private String name;

  @JsonProperty("media_type")
  private String mediaType;

  @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
      include = JsonTypeInfo.As.EXISTING_PROPERTY,
      property = "media_type", visible = true)
  @JsonSubTypes({
      @JsonSubTypes.Type(value = MovieSearchDto.class, name = "movie"),
      @JsonSubTypes.Type(value = TvSearchDto.class, name = "tv")
  })
  @JsonProperty("known_for")
  private List<MediaTypeDto> knownFor;

}
