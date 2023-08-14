package com.personal.contentservice.lock;

import static com.personal.contentservice.exception.ErrorCode.ALREADY_EXISTS_EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.personal.contentservice.dto.user.SignUpDto;
import com.personal.contentservice.exception.CustomException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LockAopAspectTest {

  @Mock
  private LockService lockService;

  @Mock
  private ProceedingJoinPoint proceedingJoinPoint;

  @InjectMocks
  private LockAopAspect lockAopAspect;

  @Test
  @DisplayName("lock & unlock 확인")
  void lockAndUnlock() throws Throwable {
    //given
    ArgumentCaptor<String> lockArgumentCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> unLockArgumentCaptor = ArgumentCaptor.forClass(String.class);
    SignUpDto.Request request = SignUpDto.Request.builder()
        .email("newEmail@example.com")
        .nickname("name")
        .password("test123!!")
        .build();

    //when
    lockAopAspect.aroundMethod(proceedingJoinPoint, request);

    //then
    verify(lockService, times(2)).lock(lockArgumentCaptor.capture());
    verify(lockService, times(2)).unlock(unLockArgumentCaptor.capture());

    // Get the captured values
    String emailLockValue = lockArgumentCaptor.getAllValues().get(0);
    String nicknameLockValue = lockArgumentCaptor.getAllValues().get(1); // Capture the second call value
    String emailUnlockValue = unLockArgumentCaptor.getAllValues().get(0);
    String nicknameUnlockValue = unLockArgumentCaptor.getAllValues().get(1); // Capture the second call value

    assertEquals("newEmail@example.com", emailLockValue);
    assertEquals("name", nicknameLockValue);
    assertEquals("newEmail@example.com", emailUnlockValue);
    assertEquals("name", nicknameUnlockValue);
  }

  @Test
  @DisplayName("lock 전에 다른 예외 발생")
  void lockAndUnlock_eventIfThrow() throws Throwable {
    //given
    ArgumentCaptor<String> lockArgumentCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> unLockArgumentCaptor = ArgumentCaptor.forClass(String.class);
    SignUpDto.Request request = SignUpDto.Request.builder()
        .email("newEmail@example.com")
        .nickname("name")
        .password("test123!!")
        .build();
    given(proceedingJoinPoint.proceed())
        .willThrow(new CustomException(ALREADY_EXISTS_EMAIL));

    //when
    assertThrows(CustomException.class,
        () -> lockAopAspect.aroundMethod(proceedingJoinPoint, request));

    //then
    verify(lockService, times(2)).lock(lockArgumentCaptor.capture());
    verify(lockService, times(2)).unlock(unLockArgumentCaptor.capture());

    // Get the captured values
    String emailLockValue = lockArgumentCaptor.getAllValues().get(0);
    String nicknameLockValue = lockArgumentCaptor.getAllValues().get(1); // Capture the second call value
    String emailUnlockValue = unLockArgumentCaptor.getAllValues().get(0);
    String nicknameUnlockValue = unLockArgumentCaptor.getAllValues().get(1); // Capture the second call value

    assertEquals("newEmail@example.com", emailLockValue);
    assertEquals("name", nicknameLockValue);
    assertEquals("newEmail@example.com", emailUnlockValue);
    assertEquals("name", nicknameUnlockValue);
  }

}