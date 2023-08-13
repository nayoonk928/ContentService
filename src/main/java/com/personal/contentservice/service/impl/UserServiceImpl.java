package com.personal.contentservice.service.impl;

import static com.personal.contentservice.exception.ErrorCode.ALREADY_EXISTS_EMAIL;
import static com.personal.contentservice.exception.ErrorCode.ALREADY_EXISTS_NICKNAME;
import static com.personal.contentservice.exception.ErrorCode.ALREADY_WITHDRAWN;
import static com.personal.contentservice.exception.ErrorCode.INCORRECT_EMAIL_OR_PASSWORD;
import static com.personal.contentservice.exception.ErrorCode.SAME_CURRENT_PASSWORD;
import static com.personal.contentservice.type.UserStatus.WITHDRAW;

import com.personal.contentservice.domain.User;
import com.personal.contentservice.dto.SignInDto;
import com.personal.contentservice.dto.SignUpDto;
import com.personal.contentservice.dto.UserUpdateDto;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.repository.UserRepository;
import com.personal.contentservice.security.jwt.JwtService;
import com.personal.contentservice.security.principal.PrincipalDetails;
import com.personal.contentservice.service.UserService;
import com.personal.contentservice.util.UserAuthenticationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  // 회원 가입
  @Override
  @Transactional
  public SignUpDto.Response signUp(SignUpDto.Request request) {
    String email = request.getEmail();
    String nickname = request.getNickname();

    if (userRepository.existsByEmail(email)) {
      throw new CustomException(ALREADY_EXISTS_EMAIL);
    }

    if (userRepository.existsByNickname(nickname)) {
      throw new CustomException(ALREADY_EXISTS_NICKNAME);
    }

    String encryptPassword = bCryptPasswordEncoder.encode(request.getPassword());

    User user = User.builder()
        .email(email)
        .nickname(nickname)
        .password(encryptPassword)
        .userType(request.getUserType())
        .build();

    userRepository.save(user);

    return SignUpDto.Response.from(user);
  }

  // 로그인 (토큰 반환)
  @Override
  public String signIn(SignInDto.Request request) {
    String email = request.getEmail();
    String password = request.getPassword();

    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(email, password);

    Authentication authentication = authenticationManager.authenticate(authenticationToken);

    if (authentication != null && authentication.isAuthenticated()) {
      PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

      User authenticatedUser = principalDetails.getUser();

      if (authenticatedUser.getUserStatus() == WITHDRAW) {
        throw new CustomException(ALREADY_WITHDRAWN);
      }

      return jwtService.generateToken(authenticatedUser);
    } else {
      throw new CustomException(INCORRECT_EMAIL_OR_PASSWORD);
    }
  }

  // 회원 탈퇴
  @Override
  @Transactional
  public void deleteUser(Authentication authentication) {
    User user = UserAuthenticationUtils.getUser(authentication);
    user.setUserStatus(WITHDRAW);
    userRepository.save(user);
  }

  // 회원 정보 수정
  @Override
  public UserUpdateDto.Response updateUserInfo(
      Authentication authentication, UserUpdateDto.Request request
  ) {
    User user = UserAuthenticationUtils.getUser(authentication);

    // 이메일 변경
    String newEmail = request.getEmail();
    if (newEmail != null) {
      if (userRepository.existsByEmail(newEmail)) {
        throw new CustomException(ALREADY_EXISTS_EMAIL);
      }
      user.setEmail(newEmail);
    }

    // 닉네임 변경
    String newNickname = request.getNickname();
    if (newNickname != null) {
      if (userRepository.existsByNickname(newNickname)) {
        throw new CustomException(ALREADY_EXISTS_NICKNAME);
      }
      user.setNickname(newNickname);
    }

    // 비밀번호 변경
    String newPassword = request.getPassword();
    if (newPassword != null) {
      if (bCryptPasswordEncoder.matches(newPassword, user.getPassword())) {
        throw new CustomException(SAME_CURRENT_PASSWORD);
      }
      user.setPassword(bCryptPasswordEncoder.encode(newPassword));
    }

    userRepository.save(user);
    return UserUpdateDto.Response.from(user);
  }

}
