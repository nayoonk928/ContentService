package com.personal.contentservice.domain;

import jakarta.persistence.Entity;
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
@Table(name = "wishlist")
public class Wishlist extends BaseEntity{

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumns({
      @JoinColumn(name = "content_id", referencedColumnName = "id", nullable = false),
      @JoinColumn(name = "media_type", referencedColumnName = "mediaType", nullable = false)
  })
  private Content content;

}
