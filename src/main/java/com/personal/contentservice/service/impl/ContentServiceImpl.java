package com.personal.contentservice.service.impl;

import static com.personal.contentservice.exception.ErrorCode.NO_RESULTS_FOUND;

import com.personal.contentservice.config.TmdbApiClient;
import com.personal.contentservice.dto.search.SearchContentDto;
import com.personal.contentservice.dto.search.api.MediaTypeDto;
import com.personal.contentservice.dto.search.api.SearchMovieResponse;
import com.personal.contentservice.dto.search.api.ApiSearchResponse;
import com.personal.contentservice.dto.search.api.SearchTvResponse;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.service.ContentService;
import com.personal.contentservice.service.GenreService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
  private final GenreService genreService;

  @Override
  @Transactional
  @Cacheable(value = "contentSearch", key = "{#query, #page}", cacheManager = "testCacheManager")
  public List<Object> searchContents(String query, int page) throws Exception {
    ApiSearchResponse response = tmdbApiClient.searchContents(query, page);

    // 현재 시간 이후에 공개되는 작품은 제외
    List<MediaTypeDto> results = response.getResults().stream()
        .filter(result -> {
          if (result instanceof SearchMovieResponse || result instanceof SearchTvResponse) {
            SearchContentDto contentDto = result.toSearchContentDto();
            return isDateBeforeNow(contentDto.getDate());
          } else {
            return true;
          }
        })
        .collect(Collectors.toList());

    Map<Long, String> genreMap = genreService.getAllGenres();
    for (MediaTypeDto dto : results) {
      dto.convertGenreIdsToNames(genreMap); // 각 DTO 에 대해 장르 이름으로 변환
    }

    List<Object> responseDtoList = new ArrayList<>();
    for (MediaTypeDto dto : results) {
      if (dto instanceof SearchMovieResponse) {
        responseDtoList.add(dto);
      } else if (dto instanceof SearchTvResponse) {
        responseDtoList.add(dto);
      }
    }

    if (results.size() == 0) {
      throw new CustomException(NO_RESULTS_FOUND);
    }

    return responseDtoList;
  }

  private boolean isDateBeforeNow(String date) {
    LocalDate parsedDate = LocalDate.parse(date);
    return parsedDate.isBefore(LocalDate.now());
  }

}