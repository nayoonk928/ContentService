package com.personal.contentservice.service;

import com.personal.contentservice.dto.user.SignInDto;
import com.personal.contentservice.dto.user.SignUpDto;
import com.personal.contentservice.dto.user.UserUpdateDto;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

  // 회원 가입
  SignUpDto.Response signUp(SignUpDto.Request request);

  // 로그인
  String signIn(SignInDto.Request request);

  // 회원 탈퇴
  void deleteUser(Authentication authentication);

  // 회원 정보 수정
  @Transactional
  UserUpdateDto.Response updateUserInfo(
      Authentication authentication, UserUpdateDto.Request request
  );

}
