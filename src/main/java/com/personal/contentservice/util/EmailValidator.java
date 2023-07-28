package com.personal.contentservice.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailValidation, String> {
  private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

  @Override
  public void initialize(EmailValidation constraintAnnotation) {
  }

  @Override
  public boolean isValid(String email, ConstraintValidatorContext context) {
    // email 이 null 인 경우 유효성 검사를 통과시킴
    if (email == null) {
      return true;
    }
    return email.matches(EMAIL_REGEX);
  }

}
