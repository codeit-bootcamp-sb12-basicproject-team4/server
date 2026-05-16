package com.codeit.findex.integrationlog.entity;

import lombok.Getter;

@Getter
public enum Result {

  SUCCESS("SUCCESS", "성공"),
  FAIL("FAIL", "실패");

  private final String value;
  private final String description;

  Result(String value, String description) {
    this.value = value;
    this.description = description;
  }

}
