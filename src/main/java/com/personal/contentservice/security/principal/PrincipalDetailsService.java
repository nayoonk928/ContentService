package com.personal.contentservice.security.principal;

import static com.personal.contentservice.exception.ErrorCode.USER_NOT_FOUND;

import com.personal.contentservice.domain.User;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(
      String email
  ) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

    return new PrincipalDetails(user);
  }

}
