package com.personal.contentservice.dto;

import com.personal.contentservice.aop.UserLockInterface;
import com.personal.contentservice.domain.User;
import com.personal.contentservice.type.UserType;
import com.personal.contentservice.util.EmailValidation;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UserUpdateDto {

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  public static class Request implements UserLockInterface {
    @EmailValidation
    private String email;

    @Size(min = 2, max = 10, message = "이름은 2자 이상 10자 이하로 입력해 주세요.")
    private String nickname;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,}$",
        message = "비밀번호는 알파벳, 숫자, 특수문자를 각각 하나 이상 포함하여 6자 이상으로 설정해주세요.")
    private String password;
  }

  @Getter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {
    private String email;
    private String nickname;
    private LocalDateTime lastModifiedAt;
    private UserType userType;

    public static Response from(User user) {
      return Response.builder()
          .email(user.getEmail())
          .nickname(user.getNickname())
          .lastModifiedAt(user.getLastModifiedAt())
          .userType(user.getUserType())
          .build();
    }
  }

}
