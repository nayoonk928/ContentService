package com.personal.contentservice.service.impl;

import com.personal.contentservice.ContentServiceApplication;
import com.personal.contentservice.config.TmdbApiClient;
import com.personal.contentservice.document.Content;
import com.personal.contentservice.domain.Genre;
import com.personal.contentservice.dto.content.api.AllContentApiResponse;
import com.personal.contentservice.dto.content.api.ContentResponse;
import com.personal.contentservice.dto.detail.CastDto;
import com.personal.contentservice.dto.detail.ContentDetailDto;
import com.personal.contentservice.dto.detail.ContentDtoContainer;
import com.personal.contentservice.dto.detail.ContentSummaryDto;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class ESContentServiceImpl implements ContentService {

  private final TmdbApiClient tmdbApiClient;
  private final ESContentRepository ESContentRepository;

  private static final Logger logger = LoggerFactory.getLogger(ContentServiceApplication.class);

  @Override
  public String saveAllContentsInfo() {
    try {
      logger.info("started to save all contents info");
      // 병렬 처리를 위한 스레드 풀 생성
      int movieThreads = 10;
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
        LocalDate endDate = i.plusYears(10).minusDays(1);

        if (endDate.isAfter(now)) {
          endDate = now;
        }

        String endDateStr = endDate.format(formatter);

        for (int j = 1; j <= movieThreads; j++) {
          final int threadId = j;
          movieExecutorService.submit(() -> {
            int moviePage = threadId;
            boolean movieTotalPage = true;
            while (movieTotalPage) {
              AllContentApiResponse movieResponse =
                  tmdbApiClient.getAllMovieInfo(moviePage, startDateStr, endDateStr);
              if (!movieResponse.getResults().isEmpty()) {
                for (ContentResponse movie : movieResponse.getResults()) {
                  MovieDetailApiResponse apiResponse = tmdbApiClient.getMovieDetail(movie.getId());
                  boolean contentExists = ESContentRepository
                      .existsByContentIdAndMediaType(movie.getId(), "movie");
                  if (!contentExists) {
                    ContentDtoContainer container = convertApiResponseToMovieDto(apiResponse);
                    saveContentInfo(container, movie, "movie");
                  }
                }
              }

              moviePage += movieThreads;
              if (movieResponse.getTotalPages() < moviePage) {
                movieTotalPage = false;
              }
            }
          });
        }
        i = i.plusYears(10);
      }

      // TV 정보 가져오기
      for (LocalDate i = startDate; i.isBefore(now); ) {
        String startDateStr = i.format(formatter);
        LocalDate endDate = i.plusYears(10).minusDays(1);

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
              if (!tvResponse.getResults().isEmpty()) {
                for (ContentResponse tv : tvResponse.getResults()) {
                  TvDetailApiResponse apiResponse = tmdbApiClient.getTvDetail(tv.getId());
                  boolean contentExists = ESContentRepository
                      .existsByContentIdAndMediaType(tv.getId(), "tv");
                  if (!contentExists) {
                    ContentDtoContainer container = convertApiResponseToTvDto(apiResponse);
                    saveContentInfo(container, tv, "tv");
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
      return "1920-01-01~" + now + "의 데이터가 Elasticsearch에 성공적으로 저장되었습니다.";
    } catch (Exception e) {
      logger.error("saveAllContentsInfo() 에러 발생 : " + e.getMessage(), e);
      return "saveAllContentsInfo() 에러 발생 : " + e.getMessage();
    }
  }

  public String saveDailyContentsInfo() {
    long totalContentCount = 0;
    long movieCount = 0;
    long tvCount = 0;

    try {
      logger.info("started to save daily contents info");

      LocalDate now = LocalDate.now();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      String nowStr = now.format(formatter);

      // Movie 정보 가져오기
      int moviePage = 1;
      boolean hasResults = true;
      while (hasResults) {
        AllContentApiResponse movieResponse = tmdbApiClient.getAllMovieInfo(moviePage, nowStr,
            nowStr);
        movieCount = movieResponse.getTotalResults();
        if (!movieResponse.getResults().isEmpty()) {
          for (ContentResponse movie : movieResponse.getResults()) {
            MovieDetailApiResponse apiResponse = tmdbApiClient.getMovieDetail(movie.getId());
            boolean contentExists = ESContentRepository
                .existsByContentIdAndMediaType(movie.getId(), "movie");
            if (!contentExists) {
              ContentDtoContainer container = convertApiResponseToMovieDto(apiResponse);
              saveContentInfo(container, movie, "movie");
              totalContentCount++;
            }
          }
          moviePage++;
        } else {
          hasResults = false;
        }
      }

      // TV 정보 가져오기
      int tvPage = 1;
      hasResults = true;
      while (hasResults) {
        AllContentApiResponse tvResponse = tmdbApiClient.getAllTvInfo(tvPage, nowStr, nowStr);
        tvCount = tvResponse.getTotalResults();
        if (!tvResponse.getResults().isEmpty()) {
          for (ContentResponse tv : tvResponse.getResults()) {
            TvDetailApiResponse apiResponse = tmdbApiClient.getTvDetail(tv.getId());
            boolean contentExists = ESContentRepository
                .existsByContentIdAndMediaType(tv.getId(), "tv");
            if (!contentExists) {
              ContentDtoContainer container = convertApiResponseToTvDto(apiResponse);
              saveContentInfo(container, tv, "tv");
              totalContentCount++;
            }
          }
        } else {
          hasResults = false;
        }
      }

      if (totalContentCount != (movieCount + tvCount)) {
        logger.warn("총 컨텐츠 개수와 저장된 컨텐츠 개수가 다릅니다");
        return "총 컨텐츠 개수과 저장된 컨텐츠 개수가 다릅니다.";
      }

      logger.info(now + "의 데이터가 Elasticsearch에 성공적으로 저장되었습니다.");
      return now + "의 데이터가 Elasticsearch에 성공적으로 저장되었습니다.";
    } catch (Exception e) {
      logger.error("saveDailyContentsInfo() 에러 발생 : " + e.getMessage(), e);
      return "saveDailyContentsInfo() 에러 발생 : " + e.getMessage();
    }
  }

  private void saveContentInfo(ContentDtoContainer container, ContentResponse response,
      String mediaType) {

    ContentSummaryDto summaryDto = container.getSummaryDto();
    ContentDetailDto detailDto = container.getDetailDto();

    if (container == null || summaryDto.getContentYear() == 0
        || summaryDto.getGenres().getNames().isEmpty()) {
      return;
    }

    if ("movie".equals(mediaType) && container.getDetailDto().getRuntime() == 0) {
      return;
    }

    Content content = new Content();
    content.setContentId(response.getId());
    content.setMediaType(mediaType);
    content.setTitle(summaryDto.getTitle());
    content.setGenres(summaryDto.getGenres());
    content.setContentYear(summaryDto.getContentYear());
    content.setDetails(detailDto);

    ESContentRepository.save(content);
  }

  private ContentDtoContainer convertApiResponseToMovieDto(MovieDetailApiResponse response) {
    List<String> productionCountries = response.getProductionCountries().stream()
        .map(ProductionCountryDto::getName)
        .collect(Collectors.toList());

    List<String> actorsInfo = getActorsInfo(response.getCredits().getCast());
    List<String> directorsName = getDirectorsName(response.getCredits().getCrew());

    String releaseDate = response.getReleaseDate();

    if (releaseDate != null && !releaseDate.isEmpty()) {
      ContentDetailDto contentDetailDto = ContentDetailDto.builder()
          .originalTitle(response.getOriginalTitle())
          .originalLanguage(response.getOriginalLanguage())
          .releaseDate(releaseDate)
          .tagline(response.getTagline())
          .overview(response.getOverview())
          .productionCountries(productionCountries)
          .runtime(response.getRuntime())
          .actorsInfo(actorsInfo)
          .directorsName(directorsName)
          .build();

      ContentSummaryDto contentSummaryDto = ContentSummaryDto.builder()
          .title(response.getTitle())
          .genres(extractGenreNames(response.getGenres()))
          .contentYear(parseDate(releaseDate))
          .build();

      return ContentDtoContainer.builder()
          .detailDto(contentDetailDto)
          .summaryDto(contentSummaryDto)
          .build();
    } else {
      return null;
    }
  }


  private ContentDtoContainer convertApiResponseToTvDto(TvDetailApiResponse response) {
    List<String> productionCountries = response.getProductionCountries().stream()
        .map(ProductionCountryDto::getName)
        .collect(Collectors.toList());

    List<String> actorsInfo = getActorsInfo(response.getCredits().getCast());
    List<String> directorsName = getDirectorsName(response.getCredits().getCrew());

    String firstAirDate = response.getFirstAirDate();
    String lastAirDate = response.getLastAirDate();

    if (firstAirDate != null && !firstAirDate.isEmpty()) {
      ContentDetailDto contentDetailDto = ContentDetailDto.builder()
          .originalTitle(response.getOriginalName())
          .originalLanguage(response.getOriginalLanguage())
          .overview(response.getOverview())
          .firstAirDate(firstAirDate)
          .lastAirDate(lastAirDate)
          .numberOfEpisodes(response.getNumberOfEpisodes())
          .numberOfSeasons(response.getNumberOfSeasons())
          .productionCountries(productionCountries)
          .seasons(response.getSeasons())
          .actorsInfo(actorsInfo)
          .directorsName(directorsName)
          .build();

      ContentSummaryDto contentSummaryDto = ContentSummaryDto.builder()
          .title(response.getName())
          .genres(extractGenreNames(response.getGenres()))
          .contentYear(parseDate(firstAirDate))
          .build();

      return ContentDtoContainer.builder()
          .detailDto(contentDetailDto)
          .summaryDto(contentSummaryDto)
          .build();
    } else {
      return null;
    }
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