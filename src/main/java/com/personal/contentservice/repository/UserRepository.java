package com.personal.contentservice.repository;

import com.personal.contentservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByUserId(String userId);

  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);
}
