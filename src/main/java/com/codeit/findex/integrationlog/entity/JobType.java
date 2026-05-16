package com.codeit.findex.integrationlog.entity;

import lombok.Getter;

@Getter
public enum JobType {

  INDEX("INDEX", "지수 정보"),
  DATA("DATA", "지수 데이터");

  private final String value;
  private final String description;

  JobType(String value, String description) {
    this.value = value;
    this.description = description;
  }
}
