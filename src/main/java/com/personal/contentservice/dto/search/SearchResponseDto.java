package com.personal.contentservice.dto.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SearchResponseDto {

    private long page;

    @JsonProperty("total_results")
    private long totalResults;

    @JsonProperty("total_pages")
    private long totalPages;

    private List<MediaTypeDto> results;

}