package com.personal.contentservice.config;

import com.personal.contentservice.domain.Genre;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TmdbApiClient {

  private static final String TMDB_API_BASE = "https://api.themoviedb.org/3/";

  @Value("#{system['tmdb.auth-token']}")
  private String authToken;

  private String getString(String apiUrl) throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(apiUrl))
        .header("accept", "application/json").header("Authorization", "Bearer " + authToken)
        .method("GET", HttpRequest.BodyPublishers.noBody()).build();
    HttpResponse<String> response = HttpClient.newHttpClient()
        .send(request, HttpResponse.BodyHandlers.ofString());
    return response.body();
  }

  public String searchContents(String query, int page) throws IOException, InterruptedException {
    String apiUrl = TMDB_API_BASE + "search/multi?query=" + query + "&language=ko&page=" + page;
    return getString(apiUrl);
  }

  public String fetchMovieGenresFromAPI() throws Exception {
    List<Genre> genres = new ArrayList<>();
    String apiUrl = TMDB_API_BASE + "genre/movie/list?language=ko";

    return getString(apiUrl);
  }

  public String fetchTvGenresFromAPI() throws Exception {
    List<Genre> genres = new ArrayList<>();
    String apiUrl = TMDB_API_BASE + "genre/tv/list?language=ko";

    return getString(apiUrl);
  }

}