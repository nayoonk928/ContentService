package com.personal.contentservice.dto.search;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiSearchDto {

    private long page;
    private long totalResults;
    private long totalPages;
    private List<Object> results;

}