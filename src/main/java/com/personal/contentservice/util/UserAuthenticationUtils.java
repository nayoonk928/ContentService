package com.personal.contentservice.util;

import static com.personal.contentservice.exception.ErrorCode.USER_NOT_FOUND;

import com.personal.contentservice.domain.User;
import com.personal.contentservice.exception.CustomException;
import com.personal.contentservice.security.principal.PrincipalDetails;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;

@UtilityClass
public class UserAuthenticationUtils {

  public User getUser(Authentication authentication) {
    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
    User user = principalDetails.getUser();
    if (user == null) {
      throw new CustomException(USER_NOT_FOUND);
    }
    return user;
  }

}
