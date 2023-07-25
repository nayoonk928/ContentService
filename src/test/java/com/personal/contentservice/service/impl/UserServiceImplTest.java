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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Transactional
class UserServiceImplTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserServiceImpl userService;

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

  @Test
  @DisplayName("동시성 테스트: 동일한 닉네임으로 동시에 가입 시 하나만 저장되는지 확인")
  void testUniqueNicknameRegistration() throws InterruptedException {
    int numThreads = 10; // 병렬로 실행할 스레드 수
    String nickname = "nickname";

    // CountDownLatch 를 사용하여 모든 스레드가 준비될 때까지 대기하도록 설정
    CountDownLatch latch = new CountDownLatch(1);

    // ExecutorService 를 사용하여 병렬로 스레드 실행
    ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

    for (int i = 0; i < numThreads; i++) {
      executorService.submit(() -> {
        try {
          // 모든 스레드가 준비될 때까지 대기
          latch.await();

          // 가입 요청 실행
          SignUpDto.Request request = SignUpDto.Request.builder()
              .email("email" + System.currentTimeMillis() + "@example.com")
              .nickname(nickname)
              .password("test123!!")
              .build();
          userService.signUp(request);

        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    }

    // 모든 스레드를 동시에 시작하도록 CountDownLatch 의 값을 0으로 설정
    latch.countDown();

    // ExecutorService 를 종료하고 모든 스레드의 작업이 완료될 때까지 기다림
    executorService.shutdown();
    executorService.awaitTermination(5, TimeUnit.SECONDS);

    // 닉네임이 중복되지 않고 하나만 저장되었는지 확인
    assertEquals(1, userRepository.countByNickname(nickname));
  }

  @Test
  @DisplayName("동시성 테스트: 동일한 이메일로 동시에 가입 시 하나만 저장되는지 확인")
  void testUniqueEmailRegistration() throws InterruptedException {
    int numThreads = 10; // 병렬로 실행할 스레드 수
    String email = "email@example.com";

    // CountDownLatch 를 사용하여 모든 스레드가 준비될 때까지 대기하도록 설정
    CountDownLatch latch = new CountDownLatch(1);

    // ExecutorService 를 사용하여 병렬로 스레드 실행
    ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

    for (int i = 0; i < numThreads; i++) {
      executorService.submit(() -> {
        try {
          // 모든 스레드가 준비될 때까지 대기
          latch.await();

          // 가입 요청 실행
          SignUpDto.Request request = SignUpDto.Request.builder()
              .email(email)
              .nickname("name" + System.currentTimeMillis())
              .password("test123!!")
              .build();
          userService.signUp(request);

        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      });
    }

    // 모든 스레드를 동시에 시작하도록 CountDownLatch 의 값을 0으로 설정
    latch.countDown();

    // ExecutorService 를 종료하고 모든 스레드의 작업이 완료될 때까지 기다림
    executorService.shutdown();
    executorService.awaitTermination(5, TimeUnit.SECONDS);

    // 닉네임이 중복되지 않고 하나만 저장되었는지 확인
    assertEquals(1, userRepository.countByEmail(email));
  }

}