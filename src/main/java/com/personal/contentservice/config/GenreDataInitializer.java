package com.personal.contentservice.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.contentservice.domain.Genre;
import com.personal.contentservice.repository.GenreRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenreDataInitializer implements CommandLineRunner {

  private final GenreRepository genreRepository;

  private final TmdbApiClient tmdbApiClient;

  @Override
  public void run(String... args) throws Exception {
    // 영화 장르 정보를 데이터베이스에 저장
    String movieGenresJson = tmdbApiClient.fetchMovieGenresFromAPI();
    saveGenresToDatabase(movieGenresJson);

    // TV 프로그램 장르 정보를 데이터베이스에 저장
    String tvGenresJson = tmdbApiClient.fetchTvGenresFromAPI();
    saveGenresToDatabase(tvGenresJson);
  }

  private void saveGenresToDatabase(String genresJson) {
    ObjectMapper objectMapper = new ObjectMapper();

    try {
      JsonNode root = objectMapper.readTree(genresJson);
      JsonNode results = root.path("genres");

      List<Genre> genres = new ArrayList<>();

      for (JsonNode result : results) {
        long id = result.get("id").asLong();
        String name = result.get("name").asText();

        // 이미 해당 id가 있는지 확인
        if (!genreRepository.existsById(id)) {
          Genre genre = new Genre(id, name);
          genres.add(genre);
        }
      }

      if (!genres.isEmpty()) {
        genreRepository.saveAll(genres);
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
