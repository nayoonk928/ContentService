package com.personal.contentservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class SignInDto {

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Request {

    @NotBlank(message = "이메일은 필수 항목 입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 항목 입니다.")
    private String password;

  }

}
