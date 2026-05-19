package com.codeit.findex.integrationlog.dto;

import java.time.Instant;
import java.time.LocalDate;
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

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate baseDateFrom;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate baseDateTo;

  private String worker;                    // 작업자

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private Instant jobTimeFrom;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
  private Instant jobTimeTo;

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
