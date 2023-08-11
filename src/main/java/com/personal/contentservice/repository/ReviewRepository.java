package com.personal.contentservice.repository;

import com.personal.contentservice.domain.Content;
import com.personal.contentservice.domain.Review;
import com.personal.contentservice.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>  {

  boolean existsByUserAndContent(User user, Content content);

  Optional<Review> findByUserAndContent(User user, Content content);

  @Query("SELECT AVG(r.rating) FROM Review r WHERE r.content = :content")
  Double calculateAverageRatingByContent(@Param("content") Content content);

}
