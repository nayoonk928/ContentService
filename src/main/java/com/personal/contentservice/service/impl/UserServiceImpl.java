package com.personal.contentservice.service.impl;

import static com.personal.contentservice.exception.ErrorCode.ALREADY_EXISTS_EMAIL;
import static com.personal.contentservice.exception.ErrorCode.ALREADY_EXISTS_NICKNAME;
import static com.personal.contentservice.exception.ErrorCode.ALREADY_EXISTS_USER_ID;
import static com.personal.contentservice.util.PasswordUtils.getEncryptPassword;

import com.personal.contentservice.domain.User;
import com.personal.contentservice.dto.SignUpDto;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.repository.UserRepository;
import com.personal.contentservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  // 회원 가입
  @Override
  @Transactional
  public ResponseEntity<?> signUp(SignUpDto signUpDto) {

    if (userRepository.existsByUserId(signUpDto.getUserId())) {
      throw new CustomException(ALREADY_EXISTS_USER_ID);
    }

    if (userRepository.existsByEmail(signUpDto.getEmail())) {
      throw new CustomException(ALREADY_EXISTS_EMAIL);
    }

    if (userRepository.existsByNickname(signUpDto.getNickname())) {
      throw new CustomException(ALREADY_EXISTS_NICKNAME);
    }

    String encryptPassword = getEncryptPassword(signUpDto.getPassword());

    User user = User.builder()
        .userId(signUpDto.getUserId())
        .email(signUpDto.getEmail())
        .nickname(signUpDto.getNickname())
        .password(encryptPassword)
        .userType(signUpDto.getUserType())
        .build();

    userRepository.save(user);
    return ResponseEntity.ok().body(user);
  }

}
