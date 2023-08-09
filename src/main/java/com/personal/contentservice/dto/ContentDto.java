package com.personal.contentservice.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentDto {

  private long id;

  private String mediaType;

  private String title;

  private int contentYear;

  private JsonNode genres;

  private double averageRating;

  private JsonNode details;

}
