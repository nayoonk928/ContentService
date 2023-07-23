package com.personal.contentservice.exception;

import static com.personal.contentservice.exception.ErrorCode.INVALID_REQUEST;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomException.class)
  public ResponseEntity<?> handleCustomException(CustomException e) {
    log.error("{} is occurred.", e.getErrorCode());
    return ResponseEntity.badRequest().body(new ErrorResponse(
        e.getErrorCode(), e.getErrorMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    BindingResult bindingResult = e.getBindingResult();
    StringBuilder errorMessage = new StringBuilder();

    for (FieldError fieldError : bindingResult.getFieldErrors()) {
      errorMessage.append(fieldError.getDefaultMessage()).append("; ");
    }

    log.error("MethodArgumentNotValidException is occurred.", e);
    return ResponseEntity.badRequest().body(
        new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescription()));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
    log.error("DataIntegrityViolationException is occurred.", e);
    return ResponseEntity.badRequest().body(
        new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescription()));
  }

}
