package com.personal.contentservice.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentSearchDto {

  private long id;
  private String title;
  private String mediaType;
  private List<String> genres;
  private int year;
}