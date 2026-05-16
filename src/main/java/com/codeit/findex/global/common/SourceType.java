package com.codeit.findex.global.common;

import lombok.Getter;

@Getter
public enum SourceType {

  USER("USER", "사용자 입력"),
  OPEN_API("OPEN_API", "공공 API");

  private final String value;
  private final String description;

  SourceType(String value, String description) {
    this.value = value;
    this.description = description;
  }
}
