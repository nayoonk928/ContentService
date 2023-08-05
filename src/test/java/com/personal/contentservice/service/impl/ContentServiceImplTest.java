package com.personal.contentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.personal.contentservice.config.TmdbApiClient;
import com.personal.contentservice.dto.search.SearchContentDto;
import com.personal.contentservice.dto.search.api.ApiSearchResponse;
import com.personal.contentservice.dto.search.api.MediaTypeDto;
import com.personal.contentservice.dto.search.api.SearchMovieResponse;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.service.GenreService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ContentServiceImplTest {

  @Mock
  private TmdbApiClient tmdbApiClient;

  @Mock
  private GenreService genreService;

  @InjectMocks
  private ContentServiceImpl contentService;

  @BeforeEach
  void setUp() throws Exception {
    tmdbApiClient = mock(TmdbApiClient.class);
    genreService = mock(GenreService.class);
    contentService = new ContentServiceImpl(tmdbApiClient, genreService);

    // Prepare dummy data
    Map<Long, String> dummyGenreMap = new HashMap<>();
    dummyGenreMap.put(1L, "Action");
    dummyGenreMap.put(2L, "Drama");

    // Mock tmdbApiClient.searchContents
    ApiSearchResponse dummyResponse = new ApiSearchResponse();
    List<MediaTypeDto> mediaTypeDtos = new ArrayList<>();
    SearchMovieResponse searchMovieResponse = new SearchMovieResponse();
    searchMovieResponse.setReleaseDate("2020-01-01");
    searchMovieResponse.setGenreIds(Collections.singletonList(1L));
    mediaTypeDtos.add(searchMovieResponse);
    dummyResponse.setResults(mediaTypeDtos);

    when(tmdbApiClient.searchContents(anyString(), anyInt())).thenReturn(dummyResponse);

    // Mock genreService.getAllGenres
    when(genreService.getAllGenres()).thenReturn(dummyGenreMap);
  }


  @Test
  @DisplayName("컨텐츠_검색_성공")
  void testSearchContents_Success() throws Exception {
    //given
    ApiSearchResponse apiSearchResponse = new ApiSearchResponse();
    SearchMovieResponse movieResponse = new SearchMovieResponse();
    movieResponse.setId(1L);
    movieResponse.setTitle("Test Movie");
    movieResponse.setMediaType("movie");
    movieResponse.setGenreIds(Collections.singletonList(1L));
    movieResponse.setReleaseDate("2023-08-05"); // Valid release date
    apiSearchResponse.setResults(Collections.singletonList(movieResponse));

    //when
    when(tmdbApiClient.searchContents(anyString(), anyInt())).thenReturn(apiSearchResponse);

    when(genreService.getAllGenres()).thenReturn(Collections.singletonMap(1L, "Action"));

    List<SearchContentDto> result = contentService.searchContents("Test", 1);

    // then
    assertEquals(1, result.size());
    SearchContentDto contentDto = result.get(0);
    assertEquals(1L, contentDto.getId());
    assertEquals("Test Movie", contentDto.getTitle());
    assertEquals("movie", contentDto.getMediaType());
    assertEquals(Collections.singletonList("Action"), contentDto.getGenreNames());
    assertEquals("2023-08-05", contentDto.getDate());
  }

  @Test
  @DisplayName("컨텐츠_검색_결과없음")
  void testSearchContents_WithNoResults() throws Exception {
    //given
    ApiSearchResponse apiSearchResponse = new ApiSearchResponse();
    apiSearchResponse.setResults(null);

    //when
    when(tmdbApiClient.searchContents(anyString(), anyInt())).thenReturn(apiSearchResponse);

    //then
    assertThrows(CustomException.class, () -> contentService.searchContents("Test", 1));
  }

}
