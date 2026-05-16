package com.codeit.findex.indexinfo.entity;

import com.codeit.findex.global.common.BaseUpdatableEntity;
import com.codeit.findex.global.common.SourceType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@Table(name = "findex")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Findex extends BaseUpdatableEntity {

  @Size(max = 100)
  @NotNull
  @Column(name = "index_name", nullable = false, length = 100)
  private String indexName;

  @Size(max = 50)
  @NotNull
  @Column(name = "index_classification", nullable = false, length = 50)
  private String indexClassification;

  @Column(name = "items_count")
  private Integer itemsCount;

  @NotNull
  @Column(name = "base_pntm", nullable = false)
  private LocalDate basePntm;

  @NotNull
  @Column(name = "base_index", nullable = false, precision = 18, scale = 2)
  private BigDecimal baseIndex;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "source_type", nullable = false, length = 20)
  private SourceType sourceType;

  @NotNull
  @Column(name = "favorite", nullable = false)
  private Boolean favorite;

}
