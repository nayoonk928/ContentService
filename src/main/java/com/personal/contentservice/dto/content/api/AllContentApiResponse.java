package com.personal.contentservice.dto.content.api;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AllContentApiResponse {

  private long page;
  private long totalResults;
  private long totalPages;
  private List<ContentResponse> results;

}
