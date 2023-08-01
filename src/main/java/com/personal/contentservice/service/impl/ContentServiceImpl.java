package com.personal.contentservice.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.contentservice.config.TmdbApiClient;
import com.personal.contentservice.domain.Genre;
import com.personal.contentservice.dto.ContentSearchDto;
import com.personal.contentservice.repository.GenreRepository;
import com.personal.contentservice.service.ContentService;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentServiceImpl implements ContentService {

  private final TmdbApiClient tmdbApiClient;
  private final GenreRepository genreRepository;

  @Override
  @Transactional
  public List<ContentSearchDto> searchContents(
      String query, int page
  ) throws IOException, InterruptedException {
    String jsonStr = tmdbApiClient.searchContents(query, page);
    ObjectMapper objectMapper = new ObjectMapper();
    List<ContentSearchDto> contentSearchDtoList = new ArrayList<>();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    try {
      JsonNode root = objectMapper.readTree(jsonStr);
      JsonNode results = root.path("results");

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

    } catch (IOException e) {
      e.printStackTrace();
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

    int id = result.get("id").asInt();

    JsonNode genre_ids = result.get("genre_ids");
    List<String> genres = new ArrayList<>();
    for (JsonNode genreIdNode : genre_ids) {
      int genreId = genreIdNode.asInt();
      Genre genre = genreRepository.findById((long) genreId).orElse(null);
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