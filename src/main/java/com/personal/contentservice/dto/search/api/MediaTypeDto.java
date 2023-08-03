package com.personal.contentservice.dto.search.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.personal.contentservice.dto.search.SearchContentDto;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "media_type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SearchMovieResponse.class, name = "movie"),
    @JsonSubTypes.Type(value = SearchTvResponse.class, name = "tv"),
    @JsonSubTypes.Type(value = SearchPersonResponse.class, name = "person")
})
public abstract class MediaTypeDto {

  public void convertGenreIdsToNames(Map<Long, String> genreMap){}

  public SearchContentDto toSearchContentDto(){return null;}

}

