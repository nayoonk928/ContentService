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
  ALREADY_EXISTS_NICKNAME("이미 존재하는 닉네임입니다.", BAD_REQUEST),
  USER_TRANSACTION_LOCK("이메일/닉네임이 이미 사용 중입니다.", BAD_REQUEST),
  INCORRECT_EMAIL_OR_PASSWORD("이메일 또는 비밀번호가 일치하지 않습니다.", BAD_REQUEST),
  USER_NOT_FOUND("사용자를 찾을 수 없습니다.", BAD_REQUEST),
  UNAUTHORIZED("인증되지 않은 사용자입니다.", BAD_REQUEST),
  SAME_CURRENT_PASSWORD("현재 비밀번호와 다른 비밀번호로 변경해주세요.", BAD_REQUEST),
  NO_RESULTS_FOUND("검색 결과가 없습니다.", BAD_REQUEST),
  INVALID_MEDIA_TYPE("옳지 않은 미디어 타입입니다.", BAD_REQUEST),
  CONTENT_NOT_FOUND("해당 컨텐츠가 존재하지 않습니다.", BAD_REQUEST),
  CONTENT_NOT_IN_WISHLIST("해당 컨텐츠가 위시리스트에 존재하지 않습니다.", BAD_REQUEST),
  REVIEW_ALREADY_EXISTS("이미 작성된 리뷰가 있습니다.", BAD_REQUEST),
  REVIEW_NOT_FOUND("해당 리뷰를 찾을 수 없습니다.", BAD_REQUEST)
  ;

  private final String description;
  private final HttpStatus httpStatus;

}
