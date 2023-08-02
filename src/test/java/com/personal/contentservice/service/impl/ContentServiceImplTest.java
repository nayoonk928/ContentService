package com.personal.contentservice.service.impl;

import static com.personal.contentservice.exception.ErrorCode.NO_RESULTS_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.personal.contentservice.config.TmdbApiClient;
import com.personal.contentservice.dto.search.MediaTypeDto;
import com.personal.contentservice.dto.search.MovieSearchDto;
import com.personal.contentservice.dto.search.SearchResponseDto;
import com.personal.contentservice.dto.search.TvSearchDto;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.service.GenreService;
import java.util.ArrayList;
import java.util.Arrays;
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
    SearchResponseDto dummyResponse = new SearchResponseDto();
    List<MediaTypeDto> mediaTypeDtos = new ArrayList<>();
    MovieSearchDto movieSearchDto = new MovieSearchDto();
    movieSearchDto.setDate("2020-01-01");
    movieSearchDto.setGenreIds(Collections.singletonList(1L));
    mediaTypeDtos.add(movieSearchDto);
    dummyResponse.setResults(mediaTypeDtos);

    when(tmdbApiClient.searchContents(anyString(), anyInt())).thenReturn(dummyResponse);

    // Mock genreService.getAllGenres
    when(genreService.getAllGenres()).thenReturn(dummyGenreMap);
  }


  @Test
  @DisplayName("컨텐츠 검색_성공")
  public void searchContentsTest_Success() throws Exception {
    //given
    String query = "test";
    int page = 1;

    SearchResponseDto responseDto = new SearchResponseDto();
    MovieSearchDto movieDto = new MovieSearchDto();
    movieDto.setId(1L);
    movieDto.setTitle("Test Movie");
    movieDto.setMediaType("movie");
    movieDto.setGenreIds(Arrays.asList(1L, 2L));
    movieDto.setDate("2020-01-01");

    TvSearchDto tvShowDto = new TvSearchDto();
    tvShowDto.setId(2L);
    tvShowDto.setTitle("Test TvShow");
    tvShowDto.setMediaType("tv");
    tvShowDto.setGenreIds(Arrays.asList(1L, 2L));
    tvShowDto.setDate("2020-01-01");

    responseDto.setResults(Arrays.asList(movieDto, tvShowDto));

    Map<Long, String> genreMap = new HashMap<>();
    genreMap.put(1L, "genre1");
    genreMap.put(2L, "genre2");

    //when
    when(tmdbApiClient.searchContents(query, page)).thenReturn(responseDto);
    when(genreService.getAllGenres()).thenReturn(genreMap);

    List<Object> result = contentService.searchContents(query, page);

    //then
    assertEquals(2, result.size());

    Object firstResult = result.get(0);
    if (firstResult instanceof MovieSearchDto.Response) {
      MovieSearchDto.Response movieResponse = (MovieSearchDto.Response) firstResult;
      assertEquals(1L, movieResponse.getId());
      assertEquals("Test Movie", movieResponse.getTitle());
      assertEquals("movie", movieResponse.getMediaType());
      assertEquals(Arrays.asList("genre1", "genre2"), movieResponse.getGenreNames());
      assertEquals("2020-01-01", movieResponse.getDate());
    }

    Object secondResult = result.get(1);
    if (secondResult instanceof TvSearchDto.Response) {
      TvSearchDto.Response tvShowResponse = (TvSearchDto.Response) secondResult;
      assertEquals(2L, tvShowResponse.getId());
      assertEquals("Test TvShow", tvShowResponse.getTitle());
      assertEquals("tv", tvShowResponse.getMediaType());
      assertEquals(Arrays.asList("genre1", "genre2"), tvShowResponse.getGenreNames());
      assertEquals("2020-01-01", tvShowResponse.getDate());
    }
  }

  @Test
  @DisplayName("컨텐츠 검색_실패_Null")
  void searchContentsTest_Fail_WithNullResponse() throws Exception {
    //given

    //when
    when(tmdbApiClient.searchContents(anyString(), anyInt())).thenReturn(null);

    //then
    assertThrows(NullPointerException.class, () -> {
      contentService.searchContents("query", 1);
    });
  }

  @Test
  @DisplayName("컨텐츠 검색_실패_결과없음")
  void searchContentsTest_Fail_WithNoResults() throws Exception {
    //given
    SearchResponseDto dummyResponse = new SearchResponseDto();
    dummyResponse.setResults(new ArrayList<>());

    //when
    when(tmdbApiClient.searchContents(anyString(), anyInt())).thenReturn(dummyResponse);

    //then
    CustomException exception = assertThrows(CustomException.class,
        () -> contentService.searchContents("query", 1));
    assertEquals(NO_RESULTS_FOUND, exception.getErrorCode());
  }

}
