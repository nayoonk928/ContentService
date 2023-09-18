package com.personal.contentservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.contentservice.dto.detail.GenreDto;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Converter
@Slf4j
public class GenreListConverter implements AttributeConverter<GenreDto, String> {

  private final ObjectMapper objectMapper = new ObjectMapper()
      .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

  @Override
  public String convertToDatabaseColumn(GenreDto genreList) {
    try {
      return objectMapper.writeValueAsString(genreList);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException("Error genre list to JSON", e);
    }
  }

  @Override
  public GenreDto convertToEntityAttribute(String json) {
    TypeReference<GenreDto> typeReference = new TypeReference<GenreDto>() {};
    try {
      return objectMapper.readValue(json, typeReference);
    } catch (IOException e) {
      throw new IllegalArgumentException("Error JSON to genre list", e);
    }
  }
}


