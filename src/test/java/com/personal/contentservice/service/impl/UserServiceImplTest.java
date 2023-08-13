package com.personal.contentservice.service.impl;

import static com.personal.contentservice.exception.ErrorCode.ALREADY_EXISTS_EMAIL;
import static com.personal.contentservice.exception.ErrorCode.ALREADY_EXISTS_NICKNAME;
import static com.personal.contentservice.exception.ErrorCode.INCORRECT_EMAIL_OR_PASSWORD;
import static com.personal.contentservice.exception.ErrorCode.SAME_CURRENT_PASSWORD;
import static com.personal.contentservice.exception.ErrorCode.USER_NOT_FOUND;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.personal.contentservice.domain.User;
import com.personal.contentservice.dto.SignInDto;
import com.personal.contentservice.dto.SignUpDto;
import com.personal.contentservice.dto.UserUpdateDto;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.repository.UserRepository;
import com.personal.contentservice.security.jwt.JwtService;
import com.personal.contentservice.security.principal.PrincipalDetails;
import com.personal.contentservice.type.UserType;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class UserServiceImplTest {

  @InjectMocks
  private UserServiceImpl userService;
  @Mock
  private UserRepository userRepository;
  @Mock
  private JwtService jwtService;
  @Mock
  private BCryptPasswordEncoder bCryptPasswordEncoder;
  @Mock
  private AuthenticationManager authenticationManager;

  @Test
  @DisplayName("회원가입_성공")
  void signUpTest_Success() {
    // given
    SignUpDto.Request request = SignUpDto.Request.builder()
        .email("test@example.com")
        .nickname("name")
        .password("test123!!")
        .userType(UserType.USER)
        .build();

    //when
    SignUpDto.Response response = userService.signUp(request);

    //then
    assertNotNull(response);
    assertEquals("test@example.com", response.getEmail());
    assertEquals("name", response.getNickname());
    assertEquals(UserType.USER, response.getUserType());
  }

  @Test
  @DisplayName("회원가입_실패_이메일중복")
  void signUpTest_DuplicateEmail() {
    //given
    SignUpDto.Request request = SignUpDto.Request.builder()
        .email("test@example.com")
        .nickname("name1")
        .password("test123!!")
        .userType(UserType.USER)
        .build();

    when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

    //when
    CustomException exception = assertThrows(CustomException.class,
        () -> userService.signUp(request));

    //then
    assertEquals(ALREADY_EXISTS_EMAIL, exception.getErrorCode());
  }

  @Test
  @DisplayName("회원가입_실패_닉네임중복")
  void signUpTest_DuplicateNickname() {
    //given
    SignUpDto.Request request = SignUpDto.Request.builder()
        .email("test@example.com")
        .nickname("name")
        .password("test123!!")
        .userType(UserType.USER)
        .build();
    given(userRepository.existsByNickname(request.getNickname())).willReturn(true);

    //when
    CustomException exception = assertThrows(CustomException.class,
        () -> userService.signUp(request));

    //then
    assertEquals(ALREADY_EXISTS_NICKNAME, exception.getErrorCode());
  }

  @Test
  @DisplayName("로그인_성공")
  void signInTest_Success() {
    //given
    String email = "test@example.com";
    String password = "test123!!";
    String token = "mockToken";
    SignInDto.Request request = new SignInDto.Request(email, password);
    User mockUser = new User();
    PrincipalDetails principalDetails = new PrincipalDetails(mockUser);
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(email, password);
    Authentication authenticated =
        new UsernamePasswordAuthenticationToken(principalDetails, null,
            principalDetails.getAuthorities());

    //when
    when(authenticationManager.authenticate(authenticationToken)).thenReturn(authenticated);
    when(jwtService.generateToken(any(User.class))).thenReturn(token);

    //then
    String result = String.valueOf(userService.signIn(request));
    assertEquals(token, result);
  }

  @Test
  @DisplayName("로그인_실패")
  void signInTest_Fail() {
    //given
    String email = "test@example.com";
    String password = "test12!!";
    SignInDto.Request request = new SignInDto.Request(email, password);
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(email, password);

    //when
    when(authenticationManager.authenticate(authenticationToken))
        .thenThrow(new CustomException(INCORRECT_EMAIL_OR_PASSWORD) {
        });

    //then
    CustomException exception = assertThrows(CustomException.class,
        () -> userService.signIn(request));
    assertEquals(INCORRECT_EMAIL_OR_PASSWORD, exception.getErrorCode());
  }

  @Test
  @DisplayName("회원탈퇴_성공")
  void deleteUserTest_Success() {
    //given
    String email = "test@example.com";
    User user = User.builder()
        .email(email)
        .build();

    //when
    PrincipalDetails principalDetails = new PrincipalDetails(user);
    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(principalDetails);

    userService.deleteUser(authentication);

    //then
    verify(userRepository, times(1)).delete(user);
  }

  @Test
  @DisplayName("회원정보수정_성공")
  void updateUserInfoTest_Success() {
    //given
    User user = User.builder()
        .email("test@example.com")
        .nickname("name")
        .password("test123!!")
        .userType(UserType.USER)
        .build();

    UserUpdateDto.Request request = UserUpdateDto.Request.builder()
        .email("test2@example.com")
        .nickname("name")
        .password("test23!!")
        .build();

    PrincipalDetails principalDetails = new PrincipalDetails(user);
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            principalDetails,  "", principalDetails.getAuthorities());

    //when
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
    when(bCryptPasswordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);
    when(bCryptPasswordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

    //then
    UserUpdateDto.Response response = userService.updateUserInfo(authentication, request);
    assertEquals(request.getNickname(), response.getNickname());
  }

  @Test
  @DisplayName("회원정보수정_실패_이메일_중복")
  void updateUserInfoTest_Failure_EmailDuplicate() {
    //given
    User user1 = User.builder()
        .email("test@example.com")
        .nickname("name")
        .password("test123!!")
        .userType(UserType.USER)
        .build();

    UserUpdateDto.Request request = UserUpdateDto.Request.builder()
        .email("test2@example.com") // 이미 존재하는 이메일을 사용하여 업데이트를 시도
        .build();

    PrincipalDetails principalDetails = new PrincipalDetails(user1);
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            principalDetails,  "", principalDetails.getAuthorities());

    //when
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
    when(userRepository.existsByEmail("test2@example.com")).thenReturn(true);

    //then
    CustomException exception = assertThrows(CustomException.class,
        () -> userService.updateUserInfo(authentication, request));
    assertEquals(ALREADY_EXISTS_EMAIL, exception.getErrorCode());
  }

  @Test
  @DisplayName("회원정보수정_실패_닉네임_중복")
  void updateUserInfoTest_Failure_NicknameDuplicate() {
    //given
    User user1 = User.builder()
        .email("test@example.com")
        .nickname("name")
        .password("test123!!")
        .userType(UserType.USER)
        .build();

    UserUpdateDto.Request request = UserUpdateDto.Request.builder()
        .nickname("name2")
        .build();

    PrincipalDetails principalDetails = new PrincipalDetails(user1);
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            principalDetails,  "", principalDetails.getAuthorities());

    //when
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
    when(userRepository.existsByNickname("name2")).thenReturn(true);

    //then
    CustomException exception = assertThrows(CustomException.class,
        () -> userService.updateUserInfo(authentication, request));
    assertEquals(ALREADY_EXISTS_NICKNAME, exception.getErrorCode());
  }

  @Test
  @DisplayName("회원정보수정_실패_동일한_비밀번호")
  void updateUserInfoTest_Failure_SamePassword() {
    //given
    String password = "test123!!";
    User user1 = User.builder()
        .email("test@example.com")
        .nickname("name")
        .password(password)
        .userType(UserType.USER)
        .build();

    UserUpdateDto.Request request = UserUpdateDto.Request.builder()
        .password(password)
        .build();

    PrincipalDetails principalDetails = new PrincipalDetails(user1);
    Authentication authentication =
        new UsernamePasswordAuthenticationToken(
            principalDetails,  "", principalDetails.getAuthorities());

    //when
    when(userRepository.findByEmail(user1.getEmail())).thenReturn(Optional.of(user1));
    when(bCryptPasswordEncoder
        .matches(request.getPassword(), user1.getPassword())).thenReturn(true);

    //then
    CustomException exception = assertThrows(CustomException.class,
        () -> userService.updateUserInfo(authentication, request));
    assertEquals(SAME_CURRENT_PASSWORD, exception.getErrorCode());
  }

}