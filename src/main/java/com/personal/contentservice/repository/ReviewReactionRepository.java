package com.personal.contentservice.repository;

import com.personal.contentservice.domain.Review;
import com.personal.contentservice.domain.ReviewReaction;
import com.personal.contentservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewReactionRepository extends JpaRepository<ReviewReaction, Long>  {

  ReviewReaction findByUserAndReview(User user, Review review);

}
