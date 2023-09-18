package com.personal.contentservice.service.impl;

import com.personal.contentservice.config.TmdbApiClient;
import com.personal.contentservice.domain.Content;
import com.personal.contentservice.domain.ContentKey;
import com.personal.contentservice.domain.Genre;
import com.personal.contentservice.domain.Review;
import com.personal.contentservice.domain.User;
import com.personal.contentservice.dto.ContentDto;
import com.personal.contentservice.dto.ReviewDto;
import com.personal.contentservice.dto.UserDto;
import com.personal.contentservice.dto.content.api.AllContentApiResponse;
import com.personal.contentservice.dto.content.api.ContentResponse;
import com.personal.contentservice.dto.detail.CastDto;
import com.personal.contentservice.dto.detail.ContentDetailDto;
import com.personal.contentservice.dto.detail.CrewDto;
import com.personal.contentservice.dto.detail.GenreDto;
import com.personal.contentservice.dto.detail.ProductionCountryDto;
import com.personal.contentservice.dto.detail.api.MovieDetailApiResponse;
import com.personal.contentservice.dto.detail.api.TvDetailApiResponse;
import com.personal.contentservice.repository.ContentRepository;
import com.personal.contentservice.service.ContentService;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentServiceImpl implements ContentService {

  private final TmdbApiClient tmdbApiClient;
  private final ContentRepository contentRepository;

  @Override
  @Transactional
  public String saveAllContentInfo() {
    try {
      // 병렬 처리를 위한 스레드 풀 생성
      int movieThreads = 15;
      ExecutorService movieExecutorService = Executors.newFixedThreadPool(movieThreads);

      int tvThreads = 5;
      ExecutorService tvExecutorService = Executors.newFixedThreadPool(tvThreads);

      // 날짜 범위 설정
      Date currentDate = new Date();
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

      // 10년 단위로 요청 범위 설정
      Date startDate = dateFormat.parse("1900-01-01");

      // Movie 정보 가져오기
      for (Date i = startDate; i.getTime() < currentDate.getTime(); ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(i);
        String startDateStr = dateFormat.format(calendar.getTime());

        calendar.add(Calendar.YEAR, 10);
        if (calendar.getTime().after(currentDate)) {
          calendar.setTime(currentDate);
        }
        String endDateStr = dateFormat.format(calendar.getTime());

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
                  ContentDetailDto contentDetailDto = convertApiResponseToMovieDto(apiResponse);
                  saveContentInfo(contentDetailDto, movie, "movie");
                }
              }

              moviePage += movieThreads;
              if (movieResponse.getTotalPages() < moviePage) break;
            }
          });
        }
        i = calendar.getTime();
      }

      // TV 정보 가져오기
      for (Date i = startDate; i.getTime() < currentDate.getTime(); ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(i);
        String startDateStr = dateFormat.format(calendar.getTime());

        calendar.add(Calendar.YEAR, 10);
        if (calendar.getTime().after(currentDate)) {
          calendar.setTime(currentDate);
        }
        String endDateStr = dateFormat.format(calendar.getTime());
        for (int j = 1; j <= tvThreads; j++) {
          final int threadId = j;
          tvExecutorService.submit(() -> {
            int tvPage = threadId;
            boolean tvTotalPage = true;
            while (tvTotalPage) {
              AllContentApiResponse tvResponse = tmdbApiClient.getAllTvInfo(tvPage, startDateStr, endDateStr);
              if (tvResponse.getResults() != null) {
                for (ContentResponse tv : tvResponse.getResults()) {
                  TvDetailApiResponse apiResponse = tmdbApiClient.getTvDetail(tv.getId());
                  ContentDetailDto contentDetailDto = convertApiResponseToTvDto(apiResponse);
                  saveContentInfo(contentDetailDto, tv, "tv");
                }
              }

              tvPage += tvThreads;
              if (tvResponse.getTotalPages() < tvPage)
                tvTotalPage = false;
            }
          });
        }
        i = calendar.getTime();
      }

      // 모든 작업이 완료될 때까지 대기
      movieExecutorService.shutdown();
      tvExecutorService.shutdown();
      movieExecutorService.awaitTermination(5, TimeUnit.MINUTES);
      tvExecutorService.awaitTermination(5, TimeUnit.MINUTES);

      return "db에 성공적으로 저장되었습니다.";
    } catch (Exception e) {
      log.info("saveAllContentInfo() 에러 발생 : " + e.getMessage(), e);
      return "에러 발생";
    }
  }

  private void saveContentInfo(ContentDetailDto contentDetailDto, ContentResponse response, String mediaType) {
    if (contentDetailDto != null) {
      Content content = new Content();
      content.setContentKey(new ContentKey());
      content.getContentKey().setIdAndMediaType(response.getId(), mediaType);
      content.setTitle(contentDetailDto.getTitle());
      content.setGenres(contentDetailDto.getGenres());
      content.setContentYear(contentDetailDto.getContentYear());
      content.setDetails(contentDetailDto);

      contentRepository.save(content);
    }
  }

  private ContentDto convertContentToDto(Content content) {
    List<ReviewDto> reviewDtos = content.getReviews().stream()
        .map(this::convertReviewToDto)
        .collect(Collectors.toList());

    return ContentDto.builder()
        .id(content.getContentKey().getId())
        .mediaType(content.getContentKey().getMediaType())
        .title(content.getTitle())
        .contentYear(content.getContentYear())
        .averageRating(content.getAverageRating())
        .genres(content.getGenres())
        .details(content.getDetails())
        .reviews(reviewDtos)
        .build();
  }

  private ReviewDto convertReviewToDto(Review review) {
    UserDto userDto = convertUserToDto(review.getUser());

    return ReviewDto.builder()
        .id(review.getId())
        .user(userDto)
        .comment(review.getComment())
        .rating(review.getRating())
        .likeCount(review.getLikeCount())
        .dislikeCount(review.getDislikeCount())
        .build();
  }

  private UserDto convertUserToDto(User user) {
    return UserDto.builder()
        .id(user.getId())
        .nickname(user.getNickname())
        .build();
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

    if (releaseDate != null) {
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

    if (firstAirDate != null) {
      dto.setContentYear(parseDate(firstAirDate));
    }

    return dto;
  }

  private int parseDate (String date) {
    if (date != null) {
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