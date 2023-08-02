package com.personal.contentservice.service.impl;

import com.personal.contentservice.domain.Genre;
import com.personal.contentservice.repository.GenreRepository;
import com.personal.contentservice.service.GenreService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

  private final GenreRepository genreRepository;

  public Map<Long, String> getAllGenres() {
    List<Genre> genres = genreRepository.findAll();
    Map<Long, String> genreMap = new HashMap<>();
    for (Genre genre : genres) {
      genreMap.put(genre.getId(), genre.getName());
    }
    return genreMap;
  }

}
