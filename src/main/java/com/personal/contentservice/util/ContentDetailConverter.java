package com.personal.contentservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.contentservice.dto.detail.ContentDetailDto;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Converter
@Slf4j
public class ContentDetailConverter implements AttributeConverter<ContentDetailDto, String> {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public String convertToDatabaseColumn(ContentDetailDto attribute) {
    try {
      log.info("ContentDetailDto to JSON start");
      String json =  objectMapper.writeValueAsString(attribute);
      return json;
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error content detail to JSON", e);
    }
  }

  @Override
  public ContentDetailDto convertToEntityAttribute(String dbData) {
    TypeReference<ContentDetailDto> typeReference = new TypeReference<ContentDetailDto>() {};
    try {
      log.info("JSON to ContentDetailDto start");
      return objectMapper.readValue(dbData, typeReference);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error JSON to content detail", e);
    }
  }

}

