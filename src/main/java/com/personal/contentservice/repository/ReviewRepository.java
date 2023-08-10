package com.personal.contentservice.repository;

import com.personal.contentservice.domain.Content;
import com.personal.contentservice.domain.Review;
import com.personal.contentservice.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>  {

  List<Review> findAllByContent(Content content);

  boolean existsByUserAndContent(User user, Content content);

  Optional<Review> findByUserAndContent(User user, Content content);

}
