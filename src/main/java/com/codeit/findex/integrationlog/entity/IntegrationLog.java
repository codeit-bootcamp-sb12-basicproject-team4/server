package com.codeit.findex.integrationlog.entity;

import com.codeit.findex.global.common.BaseEntity;
import com.codeit.findex.indexinfo.entity.Findex;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@Table(name = "integration_log")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class IntegrationLog extends BaseEntity {

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "job_type", nullable = false, length = 20)
  private JobType jobType;

  @NotNull
  @Column(name = "target_date", nullable = false)
  private LocalDate targetDate;

  @Size(max = 50)
  @NotNull
  @Column(name = "worker", nullable = false, length = 50)
  private String worker;

  @NotNull
  @Column(name = "job_time", nullable = false)
  private Instant jobTime;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "result", nullable = false, length = 10)
  private Result result;

  @NotNull
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "findex_id", nullable = false)
  private Findex findex;

}
