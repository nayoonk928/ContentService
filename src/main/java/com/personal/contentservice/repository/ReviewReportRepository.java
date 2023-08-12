package com.personal.contentservice.repository;

import com.personal.contentservice.domain.Review;
import com.personal.contentservice.domain.ReviewReport;
import com.personal.contentservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {

  boolean existsByUserAndReview(User user, Review review);

}
