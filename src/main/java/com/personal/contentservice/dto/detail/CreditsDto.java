package com.personal.contentservice.dto.detail;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditsDto {

  private List<CastDto> cast;
  private List<CrewDto> crew;

}
