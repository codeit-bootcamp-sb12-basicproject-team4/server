package com.codeit.findex.global.common;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "단위 기간 유형")
public enum PeriodType {
  DAILY,
  WEEKLY,
  MONTHLY

}
