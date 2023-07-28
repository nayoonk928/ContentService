package com.personal.contentservice.repository;

import com.personal.contentservice.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);

  Optional<User> findByEmail(String email);

  // 테스트 코드
  int countByNickname(String nickname);

  int countByEmail(String email);

}
