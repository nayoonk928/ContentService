package com.personal.contentservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ErrorResponse {

  private ErrorCode errorCode;
  private String errorMessage;

}
