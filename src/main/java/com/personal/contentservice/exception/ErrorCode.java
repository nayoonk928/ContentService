package com.personal.contentservice.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

  INTERNAL_SERVER_ERROR("내부 서버 오류가 발생했습니다.", BAD_REQUEST),
  INVALID_REQUEST("잘못된 요청입니다.", BAD_REQUEST),
  ALREADY_EXISTS_EMAIL("이미 가입된 이메일입니다.", BAD_REQUEST),
  ALREADY_EXISTS_USER_ID("이미 가입된 아이디입니다.", BAD_REQUEST),
  ALREADY_EXISTS_NICKNAME("이미 존재하는 닉네임입니다.", BAD_REQUEST)
  ;

  private final String description;
  private final HttpStatus httpStatus;

}
