package com.personal.contentservice.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "review")
public class Review extends BaseEntity {

  @JsonManagedReference
  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private User user;

  @JsonManagedReference
  @ManyToOne
  @JoinColumns({
      @JoinColumn(name = "content_id", referencedColumnName = "id", nullable = false),
      @JoinColumn(name = "media_type", referencedColumnName = "mediaType", nullable = false)
  })
  private Content content;

  @Column(nullable = false)
  private String comment;

  @Column(nullable = false)
  private double rating;

}
