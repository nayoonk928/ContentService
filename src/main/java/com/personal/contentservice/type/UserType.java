package com.personal.contentservice.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserType {

  USER("ROLE_USER"),
  ADMIN("ROLE_ADMIN");

  private final String key;
}
