package com.personal.contentservice.dto.detail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreDto {

  private List<String> names;

  @JsonCreator
  public GenreDto(String value) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    GenreDto root = objectMapper.readValue(value, GenreDto.class);

    this.names = root.getNames();
  }

}