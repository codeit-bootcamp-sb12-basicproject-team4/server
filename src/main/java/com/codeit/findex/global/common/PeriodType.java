package com.codeit.findex.global.common;

import lombok.Getter;

@Getter
public enum PeriodType {
  DAILY("일별"),
  WEEKLY("주별"),
  MONTHLY("월별"),
  QUARTERLY("분기별"),
  YEARLY("연별");

  private final String description;

  PeriodType(String description) {
    this.description = description;
  }
}

