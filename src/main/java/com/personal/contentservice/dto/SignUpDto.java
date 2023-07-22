package com.personal.contentservice.dto;

import static com.personal.contentservice.type.UserType.USER;

import com.personal.contentservice.type.UserType;
import com.personal.contentservice.util.EmailValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SignUpDto {

  @NotBlank(message = "아이디는 필수 항목 입니다.")
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$",
      message = "아이디는 알파벳과 숫자를 각각 하나 이상 포함하여 6자 이상으로 설정해주세요.")
  private String userId;

  @NotBlank(message = "이메일은 필수 항목 입니다.")
  @EmailValidation()
  private String email;

  @NotBlank(message = "닉네임은 필수 항목 입니다.")
  @Size(min = 2, max = 8, message = "이름은 2자 이상 8자 이하로 입력해 주세요.")
  private String nickname;

  @NotBlank(message = "비밀번호는 필수 항목 입니다.")
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{6,}$",
      message = "비밀번호는 알파벳, 숫자, 특수문자를 각각 하나 이상 포함하여 6자 이상으로 설정해주세요.")
  private String password;

  @NotNull(message = "회원 유형은 필수 항목 입니다.")
  @Builder.Default
  private UserType userType = USER;

}
