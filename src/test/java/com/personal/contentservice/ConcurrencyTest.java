package com.personal.contentservice;

import static com.personal.contentservice.type.UserType.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.personal.contentservice.domain.User;
import com.personal.contentservice.dto.SignUpDto;
import com.personal.contentservice.dto.UserUpdateDto;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.repository.UserRepository;
import com.personal.contentservice.security.jwt.JwtService;
import com.personal.contentservice.security.principal.PrincipalDetails;
import com.personal.contentservice.service.impl.UserServiceImpl;
import jakarta.transaction.Transactional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
@Transactional
class ConcurrencyTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserServiceImpl userService;

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

  @Test
  @DisplayName("동시성 테스트: 동시에 이메일 업데이트와 회원 가입을 시도하면 하나만 성공하는지 확인")
  void testConcurrentEmailUpdate() throws InterruptedException {
    int numThreads = 2; // 병렬로 실행할 스레드 수
    String newEmail = "newEmail@example.com";

    User user = userRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException("User not found"));
    PrincipalDetails principalDetails = new PrincipalDetails(user);

    // CountDownLatch 를 사용하여 모든 스레드가 준비될 때까지 대기하도록 설정
    CountDownLatch latch = new CountDownLatch(1);

    // ExecutorService 를 사용하여 병렬로 스레드 실행
    ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

    // Thread for signUp operation
    executorService.submit(() -> {
      try {
        // 모든 스레드가 준비될 때까지 대기
        latch.await();

        // Sign up request
        SignUpDto.Request signUpRequest = new SignUpDto.Request();
        signUpRequest.setEmail(newEmail);
        signUpRequest.setNickname("newNickname");
        signUpRequest.setPassword("test123!!");

        userService.signUp(signUpRequest);

      } catch (InterruptedException | CustomException e) {
        e.printStackTrace();
      }
    });

    // Thread for updateUserInfo operation
    executorService.submit(() -> {
      try {
        // 모든 스레드가 준비될 때까지 대기
        latch.await();

        // User update request
        UserUpdateDto.Request updateUserRequest = new UserUpdateDto.Request();
        updateUserRequest.setEmail(newEmail);

        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        userService.updateUserInfo(authentication, updateUserRequest);

      } catch (InterruptedException | CustomException e) {
        e.printStackTrace();
      }
    });

    // 모든 스레드를 동시에 시작하도록 CountDownLatch 의 값을 0으로 설정
    latch.countDown();

    // ExecutorService 를 종료하고 모든 스레드의 작업이 완료될 때까지 기다림
    executorService.shutdown();
    executorService.awaitTermination(5, TimeUnit.SECONDS);

    // 이메일이 중복되지 않고 하나만 저장되었는지 확인
    assertEquals(1, userRepository.countByEmail(newEmail));
  }

}
