package com.personal.contentservice.service.impl;

import static com.personal.contentservice.exception.ErrorCode.ALREADY_EXISTS_EMAIL;
import static com.personal.contentservice.exception.ErrorCode.ALREADY_EXISTS_NICKNAME;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.personal.contentservice.domain.User;
import com.personal.contentservice.dto.SignUpDto;
import com.personal.contentservice.dto.SignUpDto.Response;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.repository.UserRepository;
import com.personal.contentservice.type.UserType;
import jakarta.transaction.Transactional;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class UserServiceImplTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private UserServiceImpl userService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("회원가입_성공")
  void signUpTest_Success() {
    // given
    SignUpDto.Request request = SignUpDto.Request.builder()
        .email("newEmail@example.com")
        .nickname("name")
        .password("test23!!")
        .userType(UserType.USER)
        .build();

    //when
    ResponseEntity<?> result = userService.signUp(request);

    //then
    assertEquals(HttpStatus.OK, result.getStatusCode());
    SignUpDto.Response response = (Response) result.getBody();
    assertNotNull(response);
    assertEquals("newEmail@example.com", response.getEmail());
    assertEquals("name", response.getNickname());
    assertEquals(UserType.USER, response.getUserType());
  }

  @Test
  @DisplayName("회원가입_실패_이메일중복")
  public void signUpTest_DuplicateEmail() {
    //given
    User user = User.builder()
        .email("newEmail@example.com")
        .nickname("name")
        .password("test23!!")
        .userType(UserType.USER)
        .build();
    userRepository.save(user);

    SignUpDto.Request request = SignUpDto.Request.builder()
        .email("newEmail@example.com")
        .nickname("name1")
        .password("test23!!")
        .userType(UserType.USER)
        .build();

    //when
    CustomException exception = assertThrows(CustomException.class,
        () -> userService.signUp(request));

    //then
    assertEquals(ALREADY_EXISTS_EMAIL, exception.getErrorCode());
  }

  @Test
  @DisplayName("회원가입_실패_닉네임중복")
  public void signUpTest_DuplicateNickname() {
    //given
    User user = User.builder()
        .email("newEmail@example.com")
        .nickname("name")
        .password("test23!!")
        .userType(UserType.USER)
        .build();
    userRepository.save(user);

    SignUpDto.Request request = SignUpDto.Request.builder()
        .email("newEmail1@example.com")
        .nickname("name")
        .password("test23!!")
        .userType(UserType.USER)
        .build();

    //when
    CustomException exception = assertThrows(CustomException.class,
        () -> userService.signUp(request));

    //then
    assertEquals(ALREADY_EXISTS_NICKNAME, exception.getErrorCode());
  }

}