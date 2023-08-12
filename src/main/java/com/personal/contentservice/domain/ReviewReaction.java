package com.personal.contentservice.domain;

import com.personal.contentservice.type.ReactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "reviewReaction")
public class ReviewReaction extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumns(@JoinColumn(name = "review_id", referencedColumnName = "id", nullable = false))
  private Review review;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReactionType reactionType;

}
