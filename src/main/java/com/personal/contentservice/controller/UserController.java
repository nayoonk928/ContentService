package com.personal.contentservice.controller;

import com.personal.contentservice.aop.UserLock;
import com.personal.contentservice.dto.SignInDto;
import com.personal.contentservice.dto.SignUpDto;
import com.personal.contentservice.dto.UserUpdateDto;
import com.personal.contentservice.service.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  // 회원 가입
  @PostMapping("/signup")
  @UserLock
  public ResponseEntity<SignUpDto.Response> userSignUp(
      @Valid @RequestBody SignUpDto.Request request
  ) {
    return ResponseEntity.ok(userService.signUp(request));
  }

  // 로그인
  @PostMapping("/signin")
  public ResponseEntity<String> userSignIn(
      @Valid @RequestBody SignInDto.Request request
  ) {
    return ResponseEntity.ok(userService.signIn(request));
  }

  // 회원 탈퇴
  @DeleteMapping
  public void withdraw(Authentication authentication) {
    userService.deleteUser(authentication);
  }

  // 회원 정보 수정
  @PutMapping
  public ResponseEntity<UserUpdateDto.Response> updateUserInfo(
      Authentication authentication,
      @Valid @RequestBody UserUpdateDto.Request request
  ) {
    return ResponseEntity.ok().body(userService.updateUserInfo(authentication, request));
  }

}
