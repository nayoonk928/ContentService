package com.personal.contentservice.dto.detail;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContentDtoContainer {

  private ContentDetailDto detailDto;
  private ContentSummaryDto summaryDto;

}
