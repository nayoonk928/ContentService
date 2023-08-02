package com.personal.contentservice.service.impl;

import static com.personal.contentservice.exception.ErrorCode.NO_RESULTS_FOUND;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.contentservice.config.TmdbApiClient;
import com.personal.contentservice.domain.Genre;
import com.personal.contentservice.dto.ContentSearchDto;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.repository.GenreRepository;
import com.personal.contentservice.service.ContentService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentServiceImpl implements ContentService {

  private final TmdbApiClient tmdbApiClient;
  private final GenreRepository genreRepository;

  @Override
  @Transactional
  @Cacheable(value = "contentSearch", key = "{#query, #page}", cacheManager = "testCacheManager")
  public List<ContentSearchDto> searchContents(String query, int page) throws Exception {
    String jsonStr = tmdbApiClient.searchContents(query, page);

    ObjectMapper objectMapper = new ObjectMapper();
    List<ContentSearchDto> contentSearchDtoList = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    JsonNode root = objectMapper.readTree(jsonStr);
    JsonNode results = root.path("results");
    int totalResults = root.path("total_results").asInt();

    if (totalResults == 0) {
      throw new CustomException(NO_RESULTS_FOUND);
    }

    try {
      for (JsonNode result : results) {
        String mediaType = result.get("media_type").asText();
        if (mediaType.equals("person") && result.has("known_for")) {
          JsonNode knownForArray = result.get("known_for");

          for (JsonNode knownFor : knownForArray) {
            ContentSearchDto contentSearchDto = extractContent(knownFor, formatter);
            if (contentSearchDto != null) {
              contentSearchDtoList.add(contentSearchDto);
            }
          }

        } else {
          ContentSearchDto contentSearchDto = extractContent(result, formatter);
          if (contentSearchDto != null) {
            contentSearchDtoList.add(contentSearchDto);
          }
        }
      }

    } catch (Exception e) {
      log.error("Error search contents: ", e);
    }

    return contentSearchDtoList;
  }

  private ContentSearchDto extractContent(JsonNode result, DateTimeFormatter formatter) {
    LocalDate date = LocalDate.now();
    String title = "";

    String mediaType = result.get("media_type").asText();
    String tempDate = "";
    if (mediaType.equals("movie")) {
      tempDate = result.get("release_date").asText();
      title = result.get("title").asText();
    } else if (mediaType.equals("tv")) {
      tempDate = result.get("first_air_date").asText();
      title = result.get("name").asText();
    }

    if (!tempDate.isEmpty()) {
      date = LocalDate.parse(tempDate, formatter);
    }

    if (date.isAfter(LocalDate.now())) {
      return null;
    }

    long id = result.get("id").asLong();

    JsonNode genre_ids = result.get("genre_ids");
    List<String> genres = new ArrayList<>();
    for (JsonNode genreIdNode : genre_ids) {
      long genreId = genreIdNode.asLong();
      Genre genre = genreRepository.findById(genreId).orElse(null);
      if (genre != null) {
        genres.add(genre.getName());
      }
    }

    ContentSearchDto contentSearchDto = ContentSearchDto.builder()
        .id(id)
        .title(title)
        .mediaType(mediaType)
        .genres(genres)
        .year(date.getYear())
        .build();

    return contentSearchDto;
  }

}