package com.personal.contentservice.service.impl;

import com.personal.contentservice.ContentServiceApplication;
import com.personal.contentservice.config.TmdbApiClient;
import com.personal.contentservice.document.Content;
import com.personal.contentservice.domain.Genre;
import com.personal.contentservice.dto.content.api.AllContentApiResponse;
import com.personal.contentservice.dto.content.api.ContentResponse;
import com.personal.contentservice.dto.detail.CastDto;
import com.personal.contentservice.dto.detail.ContentDetailDto;
import com.personal.contentservice.dto.detail.CrewDto;
import com.personal.contentservice.dto.detail.GenreDto;
import com.personal.contentservice.dto.detail.ProductionCountryDto;
import com.personal.contentservice.dto.detail.api.MovieDetailApiResponse;
import com.personal.contentservice.dto.detail.api.TvDetailApiResponse;
import com.personal.contentservice.repository.ESContentRepository;
import com.personal.contentservice.service.ContentService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ESContentServiceImpl implements ContentService {

  private final TmdbApiClient tmdbApiClient;
  private final ESContentRepository ESContentRepository;

  private static final Logger logger = LoggerFactory.getLogger(ContentServiceApplication.class);

  @Override
  @Transactional
  public void saveAllContentsInfo() {
    try {
      logger.info("started to save all content info");
      // 병렬 처리를 위한 스레드 풀 생성
      int movieThreads = 15;
      ExecutorService movieExecutorService = Executors.newFixedThreadPool(movieThreads);

      int tvThreads = 5;
      ExecutorService tvExecutorService = Executors.newFixedThreadPool(tvThreads);

      // 날짜 범위 설정
      LocalDate now = LocalDate.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

      // 10년 단위로 요청 범위 설정
      LocalDate startDate = LocalDate.parse("1920-01-01", formatter);

      // Movie 정보 가져오기
      for (LocalDate i = startDate; i.isBefore(now); ) {
        String startDateStr = i.format(formatter);
        log.info("movie startDate: " +startDateStr);

        LocalDate endDate = i.plusYears(10);
        if (endDate.isAfter(now)) {
          endDate = now;
        }
        String endDateStr = endDate.format(formatter);

        for (int j = 1; j <= movieThreads; j++) {
          final int threadId = j;
          movieExecutorService.submit(() -> {
            int moviePage = threadId;

            while (true) {
              AllContentApiResponse movieResponse =
                  tmdbApiClient.getAllMovieInfo(moviePage, startDateStr, endDateStr);
              if (movieResponse.getResults() != null) {
                for (ContentResponse movie : movieResponse.getResults()) {
                  MovieDetailApiResponse apiResponse = tmdbApiClient.getMovieDetail(movie.getId());
                  boolean contentExists = ESContentRepository
                      .existsByContentIdAndMediaType(movie.getId(), "movie");
                  if (!contentExists) {
                    ContentDetailDto contentDetailDto = convertApiResponseToMovieDto(apiResponse);
                    saveContentInfo(contentDetailDto, movie, "movie");
                  }
                }
              }

              moviePage += movieThreads;
              if (movieResponse.getTotalPages() < moviePage) {
                break;
              }
            }
          });
        }
        i = i.plusYears(10);
      }

      // TV 정보 가져오기
      for (LocalDate i = startDate; i.isBefore(now); ) {
        String startDateStr = i.format(formatter);
        log.info("tv startDate: " +startDateStr);
        LocalDate endDate = i.plusYears(10);
        if (endDate.isAfter(now)) {
          endDate = now;
        }
        String endDateStr = endDate.format(formatter);

        for (int j = 1; j <= tvThreads; j++) {
          final int threadId = j;
          tvExecutorService.submit(() -> {
            int tvPage = threadId;
            boolean tvTotalPage = true;
            while (tvTotalPage) {
              AllContentApiResponse tvResponse = tmdbApiClient.getAllTvInfo(tvPage, startDateStr,
                  endDateStr);
              if (tvResponse.getResults() != null) {
                for (ContentResponse tv : tvResponse.getResults()) {
                  TvDetailApiResponse apiResponse = tmdbApiClient.getTvDetail(tv.getId());
                  boolean contentExists = ESContentRepository
                      .existsByContentIdAndMediaType(tv.getId(), "tv");
                  if (!contentExists) {
                    ContentDetailDto contentDetailDto = convertApiResponseToTvDto(apiResponse);
                    saveContentInfo(contentDetailDto, tv, "tv");
                  }
                }
              }

              tvPage += tvThreads;
              if (tvResponse.getTotalPages() < tvPage) {
                tvTotalPage = false;
              }
            }
          });
        }
        i = i.plusYears(10);
      }

      // 모든 작업이 완료될 때까지 대기
      movieExecutorService.shutdown();
      tvExecutorService.shutdown();
      movieExecutorService.awaitTermination(5, TimeUnit.MINUTES);
      tvExecutorService.awaitTermination(5, TimeUnit.MINUTES);

      logger.info("1920-01-01~" + now + "의 데이터가 Elasticsearch에 성공적으로 저장되었습니다.");
    } catch (Exception e) {
      logger.error("saveAllContentsInfo() 에러 발생 : " + e.getMessage(), e);
    }
  }

  private void saveContentInfo(ContentDetailDto contentDetailDto, ContentResponse response,
      String mediaType) {

    if (contentDetailDto == null || contentDetailDto.getContentYear() == 0
        || contentDetailDto.getGenres().getNames().isEmpty()) {
      return;
    }

    if ("movie".equals(mediaType) && contentDetailDto.getRuntime() == 0) {
      return;
    }

    Content content = new Content();
    content.setContentId(response.getId());
    content.setMediaType(mediaType);
    content.setTitle(contentDetailDto.getTitle());
    content.setGenres(contentDetailDto.getGenres());
    content.setContentYear(contentDetailDto.getContentYear());
    content.setDetails(contentDetailDto);

    ESContentRepository.save(content);
  }

  private ContentDetailDto convertApiResponseToMovieDto(MovieDetailApiResponse response) {
    List<String> productionCountries = response.getProductionCountries().stream()
        .map(ProductionCountryDto::getName)
        .collect(Collectors.toList());

    List<String> actorsInfo = getActorsInfo(response.getCredits().getCast());
    List<String> directorsName = getDirectorsName(response.getCredits().getCrew());

    String releaseDate = response.getReleaseDate();

    ContentDetailDto dto = ContentDetailDto.builder()
        .originalTitle(response.getOriginalTitle())
        .originalLanguage(response.getOriginalLanguage())
        .tagline(response.getTagline())
        .overview(response.getOverview())
        .productionCountries(productionCountries)
        .releaseDate(releaseDate)
        .runtime(response.getRuntime())
        .actorsInfo(actorsInfo)
        .directorsName(directorsName)
        .build();

    dto.setTitle(response.getTitle());
    dto.setGenres(extractGenreNames(response.getGenres()));

    if (releaseDate != null && !releaseDate.isEmpty()) {
      dto.setContentYear(parseDate(releaseDate));
    }

    return dto;
  }

  private ContentDetailDto convertApiResponseToTvDto(TvDetailApiResponse response) {
    List<String> productionCountries = response.getProductionCountries().stream()
        .map(ProductionCountryDto::getName)
        .collect(Collectors.toList());

    List<String> actorsInfo = getActorsInfo(response.getCredits().getCast());
    List<String> directorsName = getDirectorsName(response.getCredits().getCrew());

    String firstAirDate = response.getFirstAirDate();

    ContentDetailDto dto = ContentDetailDto.builder()
        .originalTitle(response.getOriginalName())
        .originalLanguage(response.getOriginalLanguage())
        .overview(response.getOverview())
        .firstAirDate(firstAirDate)
        .lastAirDate(response.getLastAirDate())
        .numberOfEpisodes(response.getNumberOfEpisodes())
        .numberOfSeasons(response.getNumberOfSeasons())
        .productionCountries(productionCountries)
        .seasons(response.getSeasons())
        .actorsInfo(actorsInfo)
        .directorsName(directorsName)
        .build();

    dto.setTitle(response.getName());
    dto.setGenres(extractGenreNames(response.getGenres()));

    if (firstAirDate != null && !firstAirDate.isEmpty()) {
      dto.setContentYear(parseDate(firstAirDate));
    }

    return dto;
  }

  private int parseDate(String date) {
    if (date != null && !date.isEmpty()) {
      try {
        return LocalDate.parse(date).getYear();
      } catch (DateTimeParseException e) {
        log.error("날짜 형식이 올바르지 않습니다: " + e);
      }
    }
    return 0;
  }

  private GenreDto extractGenreNames(List<Genre> genres) {
    return GenreDto.builder()
        .names(genres.stream().map(Genre::getName)
            .collect(Collectors.toList())).build();
  }

  private List<String> getActorsInfo(List<CastDto> cast) {
    return cast.stream()
        .map(castDto -> castDto.getName() + " (" + castDto.getCharacter() + ")")
        .collect(Collectors.toList());
  }

  private List<String> getDirectorsName(List<CrewDto> crew) {
    return crew.stream()
        .filter(credit -> "Director".equals(credit.getJob())
            || "Producer".equals(credit.getJob()))
        .map(CrewDto::getName)
        .collect(Collectors.toList());
  }

}