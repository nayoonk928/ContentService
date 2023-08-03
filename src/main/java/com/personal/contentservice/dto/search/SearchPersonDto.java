package com.personal.contentservice.dto.search;

import com.personal.contentservice.dto.search.api.MediaTypeDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SearchPersonDto {

  private long id;
  private String name;
  private String mediaType;
  private List<MediaTypeDto> knownFor;

}
