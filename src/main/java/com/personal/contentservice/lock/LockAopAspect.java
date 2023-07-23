package com.personal.contentservice.lock;

import com.personal.contentservice.aop.UserLockInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LockAopAspect {

  private final LockService lockService;

  @Around("@annotation(com.personal.contentservice.aop.UserLock) && args(request)")
  public Object aroundMethod(
      ProceedingJoinPoint pjp,
      UserLockInterface request
  ) throws Throwable {
    // lock 취득 시도
    lockService.lock(request.getEmail());
    lockService.lock(request.getNickname());
    try {
      return pjp.proceed();
    } finally {
      // lock 해제
      lockService.unlock(request.getEmail());
      lockService.unlock(request.getNickname());
    }
  }

}
