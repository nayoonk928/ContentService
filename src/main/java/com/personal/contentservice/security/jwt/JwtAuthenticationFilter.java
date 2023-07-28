package com.personal.contentservice.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

  private final JwtService jwtService;

  public JwtAuthenticationFilter(
      AuthenticationManager authenticationManager, JwtService jwtService) {
    super(authenticationManager);
    this.jwtService = jwtService;
  }

  @Override
  public void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain
  ) throws IOException, ServletException {
    String token = jwtService.extractTokenFromRequest(request);
    if (token != null && jwtService.validateToken(token)) {
      // 토큰이 유효한 경유 유저 정보 받기
      Authentication authentication = jwtService.getAuthentication(token);
      // 인증 성공한 경우, SecurityContextHolder 에 인증 객체 설정
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    chain.doFilter(request, response); // 다음 필터로 이동
  }



}
