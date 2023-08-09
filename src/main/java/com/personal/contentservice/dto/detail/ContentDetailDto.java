package com.personal.contentservice.dto.detail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class ContentDetailDto {

  @JsonIgnore
  private String title;

  @JsonIgnore
  private List<String> genres;

  @JsonIgnore
  private int contentYear;

}