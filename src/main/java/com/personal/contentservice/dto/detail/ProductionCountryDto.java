package com.personal.contentservice.dto.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductionCountryDto {

  @JsonProperty("iso_3166_1")
  private String iso31661;

  private String name;

}
