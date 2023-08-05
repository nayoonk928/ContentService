package com.personal.contentservice.service.impl;

import com.personal.contentservice.domain.Genre;
import com.personal.contentservice.repository.GenreRepository;
import com.personal.contentservice.service.GenreService;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

  private final GenreRepository genreRepository;

  public Map<Long, String> getAllGenres() {
    Map<Long, String> genreMap = genreRepository.findAll().stream().collect(
        Collectors.toMap(Genre::getId, Genre::getName));
    return genreMap;
  }

}
