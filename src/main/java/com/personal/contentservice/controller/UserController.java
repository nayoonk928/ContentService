package com.personal.contentservice.controller;

import com.personal.contentservice.dto.SignUpDto;
import com.personal.contentservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/signup")
  public ResponseEntity<?> userSignUp(
      @Valid @RequestBody SignUpDto signUpDto
  ) {
    return ResponseEntity.ok().body(userService.signUp(signUpDto));
  }

}
