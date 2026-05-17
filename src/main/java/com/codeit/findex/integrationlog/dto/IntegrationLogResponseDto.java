package com.codeit.findex.integrationlog.dto;

import com.codeit.findex.integrationlog.entity.JobType;
import com.codeit.findex.integrationlog.entity.Result;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegrationLogResponseDto {
  private UUID id;
  private JobType jobType;
  private UUID indexInfoId;
  private LocalDate targetDate;
  private String worker;
  private Instant jobTime;
  private Result result;
}
