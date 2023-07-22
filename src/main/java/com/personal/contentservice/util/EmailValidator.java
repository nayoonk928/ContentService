package com.personal.contentservice.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailValidator implements ConstraintValidator<EmailValidation, String> {

  private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
  private static final String EMAIL_DOMAIN = ".com";

  @Override
  public void initialize(EmailValidation constraintAnnotation) {
  }

  @Override
  public boolean isValid(String email, ConstraintValidatorContext context) {

    // 이메일 형식에 골뱅이(@)와 .com 도메인이 있는지 확인합니다.
    boolean hasAtSymbol = email.contains("@");
    boolean hasDomain = email.endsWith(EMAIL_DOMAIN);

    return hasAtSymbol && hasDomain;
  }
}
