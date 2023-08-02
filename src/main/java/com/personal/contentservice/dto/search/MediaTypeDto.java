package com.personal.contentservice.dto.search;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "media_type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = MovieSearchDto.class, name = "movie"),
    @JsonSubTypes.Type(value = TvSearchDto.class, name = "tv"),
    @JsonSubTypes.Type(value = PersonSearchDto.class, name = "person")
})
public abstract class MediaTypeDto {

  public void convertGenreIdsToNames(Map<Long, String> genreMap){}


}

