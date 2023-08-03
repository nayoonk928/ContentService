package com.personal.contentservice.dto.search.api;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiSearchResponse {

    private long page;
    private long totalResults;
    private long totalPages;
    private List<MediaTypeDto> results;

}