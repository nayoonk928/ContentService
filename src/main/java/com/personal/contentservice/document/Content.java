package com.personal.contentservice.document;

import com.personal.contentservice.domain.Review;
import com.personal.contentservice.dto.detail.ContentDetailDto;
import com.personal.contentservice.dto.detail.GenreDto;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "content_index")
public class Content {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Field(type = FieldType.Long, name = "contentId")
  private Long contentId;

  @Field(type = FieldType.Keyword, name = "mediaType")
  private String mediaType;

  @Field(type = FieldType.Text, name = "title")
  private String title;

  @Field(type = FieldType.Integer, name = "year")
  private int contentYear;

  @Field(type = FieldType.Nested, name = "genres")
  private GenreDto genres;

  @Field(type = FieldType.Double, name = "averageRating")
  private double averageRating;

  @Field(type = FieldType.Nested, name = "details")
  private ContentDetailDto details;

  @OneToMany(mappedBy = "content")
  private List<Review> reviews = new ArrayList<>();

}
