package com.personal.contentservice.service.impl;

import static com.personal.contentservice.exception.ErrorCode.INVALID_MEDIA_TYPE;
import static com.personal.contentservice.exception.ErrorCode.NO_RESULTS_FOUND;

import com.personal.contentservice.config.TmdbApiClient;
import com.personal.contentservice.domain.Content;
import com.personal.contentservice.domain.ContentKey;
import com.personal.contentservice.domain.Genre;
import com.personal.contentservice.domain.Review;
import com.personal.contentservice.domain.User;
import com.personal.contentservice.dto.ContentDto;
import com.personal.contentservice.dto.ReviewDto;
import com.personal.contentservice.dto.UserDto;
import com.personal.contentservice.dto.detail.CastDto;
import com.personal.contentservice.dto.detail.ContentDetailDto;
import com.personal.contentservice.dto.detail.CrewDto;
import com.personal.contentservice.dto.detail.GenreDto;
import com.personal.contentservice.dto.detail.ProductionCountryDto;
import com.personal.contentservice.dto.detail.api.MovieDetailApiResponse;
import com.personal.contentservice.dto.detail.api.TvDetailApiResponse;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.repository.ContentRepository;
import com.personal.contentservice.service.ContentDetailService;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentDetailServiceImpl implements ContentDetailService {

  private final TmdbApiClient tmdbApiClient;
  private final ContentRepository contentRepository;

  @Override
  @Transactional
  public ContentDto getContentDetail(long id, String mediaType) throws Exception {
    Content dbContent =
        contentRepository.findByContentKey_IdAndContentKey_MediaType(id, mediaType);

    if (dbContent != null) {
      return convertContentToDto(dbContent);
    } else {
      ContentDetailDto contentDetailDto = null;

      if ("movie".equals(mediaType)) {
        MovieDetailApiResponse apiResponse = tmdbApiClient.getMovieDetail(id);
        if (apiResponse != null) {
          contentDetailDto = convertApiResponseToMovieDto(apiResponse);
        }
      } else if ("tv".equals(mediaType)) {
        TvDetailApiResponse apiResponse = tmdbApiClient.getTvDetail(id);
        if (apiResponse != null) {
          contentDetailDto = convertApiResponseToTvDto(apiResponse);
        }
      } else {
        throw new CustomException(INVALID_MEDIA_TYPE);
      }

      if (contentDetailDto != null) {
        Content content = new Content();
        content.setContentKey(new ContentKey());
        content.getContentKey().setIdAndMediaType(id, mediaType);

        content.setTitle(contentDetailDto.getTitle());
        content.setGenres(contentDetailDto.getGenres());
        content.setContentYear(contentDetailDto.getContentYear());
        content.setDetails(contentDetailDto);

        contentRepository.save(content);

        return convertContentToDto(content);
      } else {
        throw new CustomException(NO_RESULTS_FOUND);
      }
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