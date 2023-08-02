package com.personal.contentservice.service.impl;

import static com.personal.contentservice.exception.ErrorCode.NO_RESULTS_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.personal.contentservice.config.TmdbApiClient;
import com.personal.contentservice.domain.Genre;
import com.personal.contentservice.dto.ContentSearchDto;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.repository.GenreRepository;
import java.util.Optional;
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
  private GenreRepository genreRepository;

  @InjectMocks
  private ContentServiceImpl contentService;

  @BeforeEach
  void setUp() {
    contentService = new ContentServiceImpl(tmdbApiClient, genreRepository);
  }

  @Test
  @DisplayName("컨텐츠 검색_성공")
  void testSearchContents_Success() throws Exception {
    String query = "test";
    int page = 1;
    String jsonStr = "{ \"results\" : [ { \"media_type\" : \"movie\", "
        + "\"release_date\" : \"2022-01-01\", \"title\" : \"Test Movie\", "
        + "\"id\" : 1, \"genre_ids\" : [1] } ], \"total_results\" : 1 }";
    when(tmdbApiClient.searchContents(query, page)).thenReturn(jsonStr);

    Genre testGenre = new Genre();
    testGenre.setName("Test Genre");
    when(genreRepository.findById(anyLong())).thenReturn(Optional.of(testGenre));

    var results = contentService.searchContents(query, page);

    assertFalse(results.isEmpty(), "결과가 비어있으면 안됩니다.");
    ContentSearchDto result = results.get(0);
    assertEquals("Test Movie", result.getTitle());
    assertEquals(2022, result.getYear());
    assertTrue(result.getGenres().contains("Test Genre"));
  }

  @Test
  @DisplayName("컨텐츠 검색_결과 없음")
  void testSearchContents_NoResults() throws Exception {
    String query = "test";
    int page = 1;
    String jsonStr = "{ \"results\" : [ ], \"total_results\" : 0 }";
    when(tmdbApiClient.searchContents(query, page)).thenReturn(jsonStr);

    CustomException exception = assertThrows(CustomException.class,
        () -> contentService.searchContents(query, page));
    assertEquals(NO_RESULTS_FOUND, exception.getErrorCode());
  }
}
