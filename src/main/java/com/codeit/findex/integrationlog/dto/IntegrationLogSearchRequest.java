package com.codeit.findex.integrationlog.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegrationLogSearchRequest {

  private String jobType;                   // JobType (INDEX, DATA)

  private UUID indexInfoId;                 // 지수 정보 ID

  @DateTimeFormat(pattern = "yyyyMMdd")
  private LocalDate baseDateFrom;           // 대상 날짜 (이상)

  @DateTimeFormat(pattern = "yyyyMMdd")
  private LocalDate baseDateTo;             // 대상 날짜 (미만)

  private String worker;                    // 작업자

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime jobTimeFrom;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private LocalDateTime jobTimeTo;

  private String status;                    // Result (SUCCESS, FAIL)

  private UUID idAfter;

  private String cursor;

  @Builder.Default
  private String sortField = "jobTime";

  @Builder.Default
  private String sortDirection = "desc";

  @Builder.Default
  private Integer size = 10;

}
