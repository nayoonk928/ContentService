package com.personal.contentservice.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.personal.contentservice.domain.User;
import com.personal.contentservice.security.principal.PrincipalDetails;
import com.personal.contentservice.security.principal.PrincipalDetailsService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtService {

  private final PrincipalDetailsService principalDetailsService;

  public static final String TOKEN_HEADER = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";

  private final long TOKEN_VALID_TIME = 1000 * 60 * 60 * 24; // 하루

  @Value("#{system['jwt.secret']}")
  private String secretKey;

  @PostConstruct
  protected void init() {
    this.secretKey = Base64.getEncoder().encodeToString(this.secretKey.getBytes());
  }

  private Algorithm getSign() {
    return Algorithm.HMAC512(secretKey);
  }

  public String generateToken(User user) {
    Long id = user.getId();
    String email = user.getEmail();
    String role = user.getUserType().name();
    Date now = new Date();

    return JWT.create()
        .withSubject(email)
        .withClaim("id", id)
        .withClaim("email", email)
        .withClaim("role", role)
        .withIssuedAt(now)
        .withExpiresAt(new Date(now.getTime() + TOKEN_VALID_TIME))
        .sign(getSign());
  }

  public Authentication getAuthentication(String token) {
    PrincipalDetails principalDetails =
        (PrincipalDetails) principalDetailsService.loadUserByUsername(this.getUserEmail(token));
    return new UsernamePasswordAuthenticationToken(principalDetails,
        "",
        principalDetails.getAuthorities());
  }

  private String getUserEmail(String token) {
    DecodedJWT jwt = getDecodedJWT(token);
    return jwt.getSubject();
  }

  public boolean validateToken(String token) {
    DecodedJWT jwt = getDecodedJWT(token);
    return jwt != null;
  }

  private DecodedJWT getDecodedJWT(String token) {
    try {
      JWTVerifier verifier = JWT.require(getSign())
          .acceptExpiresAt(TOKEN_VALID_TIME)
          .build();
      return verifier.verify(token);
    } catch (Exception e) {
      // 토큰 만료, 서명 검증 실패, 또는 유효하지 않은 토큰일 경우 예외가 발생
      return null;
    }
  }

  public String extractTokenFromRequest(HttpServletRequest request) {
    String header = request.getHeader(TOKEN_HEADER);
    if (header != null && header.startsWith(TOKEN_PREFIX)) {
      return header.substring(TOKEN_PREFIX.length()); // "Bearer " 부분을 제외한 토큰 값 반환
    }
    return null;
  }

}