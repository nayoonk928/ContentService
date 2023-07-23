package com.personal.contentservice.service.impl;

import static com.personal.contentservice.exception.ErrorCode.ALREADY_EXISTS_EMAIL;
import static com.personal.contentservice.exception.ErrorCode.ALREADY_EXISTS_NICKNAME;

import com.personal.contentservice.domain.User;
import com.personal.contentservice.dto.SignUpDto;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.repository.UserRepository;
import com.personal.contentservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  // 회원 가입
  @Override
  @Transactional
  public ResponseEntity<SignUpDto.Response> signUp(SignUpDto.Request request) {
    String email = request.getEmail();
    String nickname = request.getNickname();

    if (userRepository.existsByEmail(email)) {
      throw new CustomException(ALREADY_EXISTS_EMAIL);
    }

    if (userRepository.existsByNickname(nickname)) {
      throw new CustomException(ALREADY_EXISTS_NICKNAME);
    }

    String encryptPassword = bCryptPasswordEncoder.encode(request.getPassword());

    User user = User.builder()
        .email(email)
        .nickname(nickname)
        .password(encryptPassword)
        .userType(request.getUserType())
        .build();

    userRepository.save(user);

    return ResponseEntity.ok().body(SignUpDto.Response.from(user));
  }

}
