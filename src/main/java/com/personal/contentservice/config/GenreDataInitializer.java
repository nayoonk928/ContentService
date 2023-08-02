package com.personal.contentservice.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.contentservice.domain.Genre;
import com.personal.contentservice.repository.GenreRepository;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GenreDataInitializer implements CommandLineRunner {

  private final GenreRepository genreRepository;

  private final TmdbApiClient tmdbApiClient;

  @Override
  public void run(String... args) throws Exception {
    // 영화 장르 정보를 데이터베이스에 저장
    HttpResponse<String> movieGenresResponse = tmdbApiClient.fetchMovieGenresFromAPI();
    saveGenresToDatabase(movieGenresResponse);

    // TV 프로그램 장르 정보를 데이터베이스에 저장
    HttpResponse<String> tvGenresResponse = tmdbApiClient.fetchTvGenresFromAPI();
    saveGenresToDatabase(tvGenresResponse);
  }

  private void saveGenresToDatabase(HttpResponse<String> genresResponse) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode root = objectMapper.readTree(genresResponse.body());
    JsonNode results = root.path("genres");

    List<Genre> genres = new ArrayList<>();

    for (JsonNode result : results) {
      long id = result.get("id").asLong();
      String name = result.get("name").asText();

      // 이미 해당 id가 있는지 확인
      if (!genreRepository.existsById(id)) {
        Genre genre = Genre.builder()
            .id(id)
            .name(name)
            .build();
        genres.add(genre);
      }
    }

    if (!genres.isEmpty()) {
      genreRepository.saveAll(genres);
    }
  }

}
