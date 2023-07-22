package com.personal.contentservice.service;

import com.personal.contentservice.dto.SignUpDto;
import org.springframework.http.ResponseEntity;

public interface UserService {

  // 회원 가입
  ResponseEntity<?> signUp(SignUpDto userSignUpDto);

}
