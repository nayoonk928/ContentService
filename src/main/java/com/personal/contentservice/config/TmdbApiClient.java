package com.personal.contentservice.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.personal.contentservice.dto.content.api.AllContentApiResponse;
import com.personal.contentservice.dto.detail.api.MovieDetailApiResponse;
import com.personal.contentservice.dto.detail.api.TvDetailApiResponse;
import com.personal.contentservice.dto.search.api.ApiSearchResponse;
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

  public ApiSearchResponse searchContents(String query, int page) throws Exception {
    URI uri = UriComponentsBuilder
        .fromUriString(tmdbApiBase)
        .path("/search/multi")
        .queryParam("query", query)
        .queryParam("language", "ko")
        .queryParam("page", page)
        .build()
        .toUri();
    HttpResponse<String> response = getResponse(uri);

    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    ApiSearchResponse apiSearchResponse =
        objectMapper.readValue(response.body(), ApiSearchResponse.class);

    return apiSearchResponse;
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

  public MovieDetailApiResponse getMovieDetail(long id) {
    try {
      URI uri = UriComponentsBuilder
          .fromUriString(tmdbApiBase)
          .path("/movie/" + id)
          .queryParam("append_to_response", "credits")
          .queryParam("language", "ko")
          .build()
          .toUri();
      HttpResponse<String> response = getResponse(uri);

      objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

      TypeReference<MovieDetailApiResponse> movieDetailTypeRef = new TypeReference<>() {
      };
      MovieDetailApiResponse movieDetailApiResponse =
          objectMapper.readValue(response.body(), movieDetailTypeRef);

      movieDetailApiResponse.setMediaType("movie");

      return movieDetailApiResponse;
    } catch (Exception e) {
      log.info("getMovieDetail() 에러 발생: " + e.getMessage(), e);
      return null;
    }
  }

  public TvDetailApiResponse getTvDetail(long id) {
    try {
      URI uri = UriComponentsBuilder
          .fromUriString(tmdbApiBase)
          .path("/tv/" + id)
          .queryParam("append_to_response", "credits")
          .queryParam("language", "ko")
          .build()
          .toUri();
      HttpResponse<String> response = getResponse(uri);

      objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

      TypeReference<TvDetailApiResponse> tvDetailTypeRef = new TypeReference<>() {
      };
      TvDetailApiResponse tvDetailApiResponse =
          objectMapper.readValue(response.body(), tvDetailTypeRef);

      tvDetailApiResponse.setMediaType("tv");

      return tvDetailApiResponse;
    } catch (Exception e) {
      log.info("getTvDetail() 에러 발생: " + e.getMessage(), e);
      return null;
    }
  }

  public AllContentApiResponse getAllMovieInfo(
      int page, String releaseDateGte, String releaseDateLte
  ) {
    try {
      URI uri = UriComponentsBuilder
          .fromUriString(tmdbApiBase)
          .path("/discover/movie")
          .queryParam("include_adult", "false")
          .queryParam("include_video", "false")
          .queryParam("language", "ko")
          .queryParam("page", page)
          .queryParam("region", "KR")
          .queryParam("primary_release_date.gte", releaseDateGte)
          .queryParam("primary_release_date.lte", releaseDateLte)
          .queryParam("with_watch_providers", "watcha")
          .build()
          .toUri();
      HttpResponse<String> response = getResponse(uri);
      objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

      TypeReference<AllContentApiResponse> allMovieResponseTypeRef = new TypeReference<>() {
      };
      AllContentApiResponse allMovieApiResponse =
          objectMapper.readValue(response.body(), allMovieResponseTypeRef);

      return allMovieApiResponse;
    } catch (Exception e) {
      log.info("getAllMovieInfo() 에러 발생: " + e.getMessage(), e);
      return null;
    }
  }

  public AllContentApiResponse getAllTvInfo(
      int page, String firstAirDateGte, String firstAirDateLte
  ) {
    try {
      URI uri = UriComponentsBuilder
          .fromUriString(tmdbApiBase)
          .path("/discover/tv")
          .queryParam("include_adult", "false")
          .queryParam("include_null_first_air_dates", "false")
          .queryParam("language", "ko")
          .queryParam("page", page)
          .queryParam("watch_region", "KR")
          .queryParam("first_air_date.gte", firstAirDateGte)
          .queryParam("first_air_date.lte", firstAirDateLte)
          .queryParam("with_watch_providers", "watcha")
          .build()
          .toUri();
      HttpResponse<String> response = getResponse(uri);

      objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

      TypeReference<AllContentApiResponse> allTvApiResponseTypeRef = new TypeReference<>() {
      };
      AllContentApiResponse allTvApiResponse =
          objectMapper.readValue(response.body(), allTvApiResponseTypeRef);

      return allTvApiResponse;
    } catch (Exception e) {
      log.info("getAllTvInfo() 에러 발생: " + e.getMessage(), e);
      return null;
    }
  }

}