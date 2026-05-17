package com.codeit.findex.integrationlog.dto;

import com.codeit.findex.global.common.SourceType;
import com.codeit.findex.indexinfo.entity.Findex;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenApiIndex {

  @JsonProperty("idxNm")
  private String indexName;

  @JsonProperty("idxCsf")
  private String indexClassification;

  @JsonProperty("epyItmsCnt")
  private Integer itemsCount;

  @JsonProperty("basPntm")
  private String basePntm; // "20240701" 형식

  @JsonProperty("basIdx")
  private BigDecimal baseIndex;

  @JsonProperty("basDt")
  private String baseDate; // "20260514" 형식

  public Findex toEntity() {
    return Findex.builder()
        .indexName(this.indexName)
        .indexClassification(this.indexClassification)
        .itemsCount(this.itemsCount)
        .basePntm(LocalDate.parse(this.basePntm, DateTimeFormatter.ofPattern("yyyyMMdd")))
        .baseIndex(this.baseIndex)
        .sourceType(SourceType.OPEN_API)
        .favorite(false)
        .build();
  }
}
