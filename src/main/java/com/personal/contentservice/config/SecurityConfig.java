package com.personal.contentservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.formLogin(formLogin -> formLogin.disable())// FormLogin 사용 X
        .httpBasic(httpBasic -> httpBasic.disable()) // httpBasic 사용 X
        .csrf(csrf -> csrf.disable()) // csrf 보안 사용 X
        .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
        // 세션 사용하지 않으므로 STATELESS 로 설정
        .sessionManagement(sessionManagement ->
            sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // URL 별 권한 관리 옵션
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/user/**").permitAll()
            .anyRequest().permitAll());
    return http.build();
  }

}
