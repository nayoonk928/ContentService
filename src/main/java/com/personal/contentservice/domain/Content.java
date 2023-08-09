package com.personal.contentservice.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.personal.contentservice.util.JsonNodeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
@Entity
@Table(name = "content")
public class Content {

  @JsonBackReference
  @OneToMany(mappedBy = "content")
  private List<Review> reviews = new ArrayList<>();

  @EmbeddedId
  private ContentKey contentKey;

  @Column(nullable = false)
  private String title;

  @Column
  private int contentYear;

  @Convert(converter = JsonNodeConverter.class)
  @Column(columnDefinition = "json")
  private JsonNode genres;

  private double averageRating;

  @Convert(converter = JsonNodeConverter.class)
  @Column(columnDefinition = "json")
  private JsonNode details;

}
