package com.personal.contentservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.personal.contentservice.config.TmdbApiClient;
import com.personal.contentservice.dto.content.api.AllContentApiResponse;
import com.personal.contentservice.dto.content.api.ContentResponse;
import com.personal.contentservice.repository.ESContentRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ESContentServiceImplTest {

  @Mock
  private TmdbApiClient tmdbApiClient;

  @Mock
  private ESContentRepository contentRepository;

  private ESContentServiceImpl contentService;

  List<ContentResponse> movieResults = new ArrayList<>();
  List<ContentResponse> tvResults = new ArrayList<>();

  LocalDate now = LocalDate.now();

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
    contentService = new ESContentServiceImpl(tmdbApiClient, contentRepository);
  }

  @Test
  void testSaveAllContentsInfo() {
    // 모의 API 응답
    AllContentApiResponse movieApiResponse = new AllContentApiResponse();
    movieApiResponse.setResults(movieResults); // Movie 정보 설정

    AllContentApiResponse tvApiResponse = new AllContentApiResponse();
    tvApiResponse.setResults(tvResults); // TV 정보 설정

    when(tmdbApiClient.getAllMovieInfo(anyInt(), anyString(), anyString()))
        .thenReturn(movieApiResponse);

    when(tmdbApiClient.getAllTvInfo(anyInt(), anyString(), anyString()))
        .thenReturn(tvApiResponse);

    // existsByContentIdAndMediaType 메서드가 항상 false 반환하도록 설정
    when(contentRepository.existsByContentIdAndMediaType(anyLong(), anyString()))
        .thenReturn(false);

    // 테스트 실행
    String result = contentService.saveAllContentsInfo();

    assertEquals("1920-01-01~"+ now +"의 데이터가 Elasticsearch에 성공적으로 저장되었습니다.", result);
  }

  @Test
  void testSaveDailyContentsInfo() {
    // 모의 API 응답
    AllContentApiResponse movieApiResponse = new AllContentApiResponse();
    movieApiResponse.setResults(movieResults); // Movie 정보 설정

    AllContentApiResponse tvApiResponse = new AllContentApiResponse();
    tvApiResponse.setResults(tvResults); // TV 정보 설정

    when(tmdbApiClient.getAllMovieInfo(anyInt(), anyString(), anyString()))
        .thenReturn(movieApiResponse);

    when(tmdbApiClient.getAllTvInfo(anyInt(), anyString(), anyString()))
        .thenReturn(tvApiResponse);

    // existsByContentIdAndMediaType 메서드가 항상 false 반환하도록 설정
    when(contentRepository.existsByContentIdAndMediaType(anyLong(), anyString()))
        .thenReturn(false);

    // 테스트 실행
    String result = contentService.saveDailyContentsInfo();

    // 결과 메시지 확인
    assertEquals(now + "의 데이터가 Elasticsearch에 성공적으로 저장되었습니다.", result);
  }

}