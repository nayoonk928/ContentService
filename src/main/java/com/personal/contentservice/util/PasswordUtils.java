package com.personal.contentservice.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@UtilityClass
public class PasswordUtils {

  // password 암호화해서 리턴하는 함수
  public static String getEncryptPassword(String password) {
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    return bCryptPasswordEncoder.encode(password);
  }

  // 입력한 패스워드를 해시된 패스워드와 비교하는 함수
  public static boolean equalPassword(String password, String encryptedPassword) {
    return BCrypt.checkpw(password, encryptedPassword);
  }
}
