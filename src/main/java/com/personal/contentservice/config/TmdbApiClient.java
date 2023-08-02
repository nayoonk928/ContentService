package com.personal.contentservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.contentservice.dto.search.SearchResponseDto;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@Slf4j
public class TmdbApiClient {

  private final HttpClient client;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Value("${tmdb.api.base}")
  private String tmdbApiBase;

  @Value("#{system['tmdb.auth-token']}")
  private String authToken;

  private HttpResponse<String> getResponse(URI apiUrl) throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(apiUrl)
        .header("accept", "application/json")
        .header("Authorization", "Bearer " + authToken)
        .method("GET", HttpRequest.BodyPublishers.noBody())
        .build();

    return client.send(request, HttpResponse.BodyHandlers.ofString());
  }

  public SearchResponseDto searchContents(String query, int page) throws Exception {
    URI uri = UriComponentsBuilder
        .fromUriString(tmdbApiBase)
        .path("/search/multi")
        .queryParam("query", query)
        .queryParam("language", "ko")
        .queryParam("page", page)
        .build()
        .toUri();
    HttpResponse<String> response = getResponse(uri);

    SearchResponseDto searchResponseDto =
        objectMapper.readValue(response.body(), SearchResponseDto.class);

    return searchResponseDto;
  }

  public HttpResponse<String> fetchMovieGenresFromAPI() throws Exception {
    URI uri = UriComponentsBuilder
        .fromUriString(tmdbApiBase)
        .path("/genre/movie/list")
        .queryParam("language", "ko")
        .build()
        .toUri();
    return getResponse(uri);
  }

  public HttpResponse<String> fetchTvGenresFromAPI() throws Exception {
    URI uri = UriComponentsBuilder
        .fromUriString(tmdbApiBase)
        .path("/genre/tv/list")
        .queryParam("language", "ko")
        .build()
        .toUri();
    return getResponse(uri);
  }

}