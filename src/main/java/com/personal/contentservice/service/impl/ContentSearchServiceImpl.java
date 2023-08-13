package com.personal.contentservice.service.impl;

import static com.personal.contentservice.exception.ErrorCode.NO_RESULTS_FOUND;

import com.personal.contentservice.config.TmdbApiClient;
import com.personal.contentservice.domain.Content;
import com.personal.contentservice.dto.search.SearchContentDto;
import com.personal.contentservice.dto.search.SearchPersonDto;
import com.personal.contentservice.dto.search.api.ApiSearchResponse;
import com.personal.contentservice.dto.search.api.MediaTypeDto;
import com.personal.contentservice.dto.search.api.SearchMovieResponse;
import com.personal.contentservice.dto.search.api.SearchPersonResponse;
import com.personal.contentservice.dto.search.api.SearchTvResponse;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.repository.ContentRepository;
import com.personal.contentservice.service.ContentSearchService;
import com.personal.contentservice.service.GenreService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentSearchServiceImpl implements ContentSearchService {

  private final TmdbApiClient tmdbApiClient;
  private final GenreService genreService;
  private final ContentRepository contentRepository;

  @Override
  @Transactional
  @Cacheable(value = "contentSearch", key = "{#query, #page}", cacheManager = "cacheManager")
  public List<SearchContentDto> searchContents(String query, int page) throws Exception {
    List<SearchContentDto> contentDtos = new ArrayList<>();
    ApiSearchResponse apiSearchResponse = tmdbApiClient.searchContents(query, page);

    if (apiSearchResponse != null && apiSearchResponse.getResults() != null) {
      for (MediaTypeDto mediaTypeDto : apiSearchResponse.getResults()) {
        List<SearchContentDto> convertedDtos = convertMediaTypeDtoToSearchContentDto(mediaTypeDto);
        for (SearchContentDto dto : convertedDtos) {
          Content dbContent =
              contentRepository.findByContentKey_IdAndContentKey_MediaType(
                  dto.getId(), dto.getMediaType());
          if (dbContent != null) {
            dto.setAverageRating(dbContent.getAverageRating());
          } else {
            dto.setAverageRating(0.0);
          }

          if (isDateBeforeNow(dto.getDate())) {
            contentDtos.add(dto);
          }
        }
      }
    }

    if (contentDtos.size() == 0) {
      throw new CustomException(NO_RESULTS_FOUND);
    }

    return contentDtos;
  }

  private boolean isDateBeforeNow(String date) {
    if (date.length() == 0) {
      return true;
    }
    LocalDate parsedDate = LocalDate.parse(date);
    return parsedDate.isBefore(LocalDate.now());
  }

  private List<SearchContentDto> convertMediaTypeDtoToSearchContentDto(MediaTypeDto mediaTypeDto) {
    if (mediaTypeDto instanceof SearchMovieResponse) {
      return Collections.singletonList(
          convertMovieResponseToSearchContentDto((SearchMovieResponse) mediaTypeDto));
    } else if (mediaTypeDto instanceof SearchTvResponse) {
      return Collections.singletonList(
          convertTvResponseToSearchContentDto((SearchTvResponse) mediaTypeDto));
    } else if (mediaTypeDto instanceof SearchPersonResponse) {
      return convertPersonResponseToSearchPersonDto(
          (SearchPersonResponse) mediaTypeDto).getKnownFor();
    }
    return Collections.emptyList();
  }

  private SearchContentDto convertMovieResponseToSearchContentDto(
      SearchMovieResponse movieResponse) {
    List<String> genreNames = getGenreNames(movieResponse.getGenreIds());
    return SearchContentDto.builder()
        .id(movieResponse.getId())
        .title(movieResponse.getTitle())
        .mediaType(movieResponse.getMediaType())
        .genreNames(genreNames)
        .date(movieResponse.getReleaseDate())
        .build();
  }

  private SearchContentDto convertTvResponseToSearchContentDto(SearchTvResponse tvResponse) {
    List<String> genreNames = getGenreNames(tvResponse.getGenreIds());
    return SearchContentDto.builder()
        .id(tvResponse.getId())
        .title(tvResponse.getName())
        .mediaType(tvResponse.getMediaType())
        .genreNames(genreNames)
        .date(tvResponse.getFirstAirDate())
        .build();
  }

  private SearchPersonDto convertPersonResponseToSearchPersonDto(
      SearchPersonResponse personResponse) {
    List<MediaTypeDto> knownFor = personResponse.getKnownFor();
    List<SearchContentDto> knownForContentDtos = new ArrayList<>();

    if (knownFor != null) {
      for (MediaTypeDto mediaTypeDto : knownFor) {
        if (mediaTypeDto instanceof SearchMovieResponse) {
          SearchContentDto contentDto =
              convertMovieResponseToSearchContentDto((SearchMovieResponse) mediaTypeDto);
          if (contentDto != null) {
            knownForContentDtos.add(contentDto);
          }
        } else if (mediaTypeDto instanceof SearchTvResponse) {
          SearchContentDto contentDto =
              convertTvResponseToSearchContentDto((SearchTvResponse) mediaTypeDto);
          if (contentDto != null) {
            knownForContentDtos.add(contentDto);
          }
        }
      }
    }

    return SearchPersonDto.builder()
        .id(personResponse.getId())
        .name(personResponse.getName())
        .mediaType(personResponse.getMediaType())
        .knownFor(knownForContentDtos)
        .build();
  }

  private List<String> getGenreNames(List<Long> genreIds) {
    Map<Long, String> genreMap = genreService.getAllGenres();
    List<String> genreNames = new ArrayList<>();
    if (genreIds != null) {
      for (Long id : genreIds) {
        String name = genreMap.get(id);
        if (name != null) {
          genreNames.add(name);
        }
      }
    }
    return genreNames;
  }

}