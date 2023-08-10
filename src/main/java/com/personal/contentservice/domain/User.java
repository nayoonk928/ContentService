package com.personal.contentservice.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.personal.contentservice.type.UserType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "user")
public class User extends BaseEntity {

  @JsonBackReference
  @OneToMany(mappedBy = "user")
  private List<Review> reviews = new ArrayList<>();

  @JsonBackReference
  @OneToMany(mappedBy = "user")
  private List<Wishlist> wishlists = new ArrayList<>();

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false, unique = true)
  private String nickname;

  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  private UserType userType;

}
