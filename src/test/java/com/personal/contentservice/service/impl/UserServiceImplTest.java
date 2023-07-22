package com.personal.contentservice.service.impl;

import static com.personal.contentservice.exception.ErrorCode.ALREADY_EXISTS_EMAIL;
import static com.personal.contentservice.exception.ErrorCode.ALREADY_EXISTS_NICKNAME;
import static com.personal.contentservice.exception.ErrorCode.ALREADY_EXISTS_USER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.personal.contentservice.domain.User;
import com.personal.contentservice.dto.SignUpDto;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.repository.UserRepository;
import com.personal.contentservice.type.UserType;
import com.personal.contentservice.util.PasswordUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class UserServiceImplTest {
  @Mock
  private UserRepository userRepository;

  private UserServiceImpl userService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    userService = new UserServiceImpl(userRepository);
  }

  @Test
  @DisplayName("회원가입_성공")
  void signUpTest_Success() {
    // given
    SignUpDto signUpDto = SignUpDto.builder()
        .userId("newUser23")
        .email("newEmail@example.com")
        .nickname("newNickname")
        .password("test23!!")
        .userType(UserType.USER)
        .build();

    when(userRepository.existsByUserId(signUpDto.getUserId())).thenReturn(false);
    when(userRepository.existsByEmail(signUpDto.getEmail())).thenReturn(false);
    when(userRepository.existsByNickname(signUpDto.getNickname())).thenReturn(false);

    //when
    ResponseEntity<?> actualResponse = userService.signUp(signUpDto);

    //then
    User actualUser = (User) actualResponse.getBody();
    assertTrue(PasswordUtils.equalPassword(signUpDto.getPassword(), actualUser.getPassword()));
    assertEquals(signUpDto.getUserId(), actualUser.getUserId());
    assertEquals(signUpDto.getEmail(), actualUser.getEmail());
    assertEquals(signUpDto.getNickname(), actualUser.getNickname());
    assertEquals(signUpDto.getUserType(), actualUser.getUserType());
  }

  @Test
  @DisplayName("회원가입_실패_아이디중복")
  public void signUpTest_DuplicateUserId() {
    //given
    User user = User.builder()
        .userId("newUser22")
        .email("newEmail1@example.com")
        .nickname("name1")
        .password("test23!!")
        .build();
    userRepository.save(user);

    SignUpDto signUpDto = SignUpDto.builder()
        .userId(user.getUserId())
        .email("newEmail@example.com")
        .nickname("name")
        .password("test23!!")
        .build();

    //when
    when(userRepository.existsByUserId(signUpDto.getUserId())).thenReturn(true);
    CustomException exception = assertThrows(CustomException.class,
        () -> userService.signUp(signUpDto));

    //then
    assertEquals(ALREADY_EXISTS_USER_ID, exception.getErrorCode());
  }

  @Test
  @DisplayName("회원가입_실패_이메일중복")
  public void signUpTest_DuplicateEmail() {
    //given
    User user = User.builder()
        .userId("newUser22")
        .email("newEmail@example.com")
        .nickname("name1")
        .password("test23!!")
        .userType(UserType.USER)
        .build();
    userRepository.save(user);

    SignUpDto signUpDto = SignUpDto.builder()
        .userId("newUser23")
        .email(user.getEmail())
        .nickname("name")
        .password("test23!!")
        .userType(UserType.USER)
        .build();

    //when
    when(userRepository.existsByEmail(signUpDto.getEmail())).thenReturn(true);
    CustomException exception = assertThrows(CustomException.class,
        () -> userService.signUp(signUpDto));

    //then
    assertEquals(ALREADY_EXISTS_EMAIL, exception.getErrorCode());
  }

  @Test
  @DisplayName("회원가입_실패_닉네임중복")
  public void signUpTest_DuplicateNickname() {
    //given
    User user = User.builder()
        .userId("newUser22")
        .email("newEmail1@example.com")
        .nickname("name")
        .password("test23!!")
        .userType(UserType.USER)
        .build();
    userRepository.save(user);

    SignUpDto signUpDto = SignUpDto.builder()
        .userId("newUser23")
        .email("newEmail@example.com")
        .nickname(user.getNickname())
        .password("test23!!")
        .userType(UserType.USER)
        .build();

    //when
    when(userRepository.existsByNickname(signUpDto.getNickname())).thenReturn(true);
    CustomException exception = assertThrows(CustomException.class,
        () -> userService.signUp(signUpDto));

    //then
    assertEquals(ALREADY_EXISTS_NICKNAME, exception.getErrorCode());
  }

}