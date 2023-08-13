package com.personal.contentservice.domain;

import com.personal.contentservice.dto.detail.ContentDetailDto;
import com.personal.contentservice.dto.detail.GenreDto;
import com.personal.contentservice.util.ContentDetailConverter;
import com.personal.contentservice.util.GenreListConverter;
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

  @OneToMany(mappedBy = "content")
  private List<Review> reviews = new ArrayList<>();

  @EmbeddedId
  private ContentKey contentKey;

  @Column(nullable = false)
  private String title;

  @Column
  private int contentYear;

  @Convert(converter = GenreListConverter.class)
  @Column(columnDefinition = "json")
  private GenreDto genres;

  @Column
  private double averageRating;

  @Convert(converter = ContentDetailConverter.class)
  @Column(columnDefinition = "json")
  private ContentDetailDto details;

}
