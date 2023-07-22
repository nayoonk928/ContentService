package com.personal.contentservice.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailValidation {

  String message() default "이메일 형식에 맞게 입력해주세요.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
