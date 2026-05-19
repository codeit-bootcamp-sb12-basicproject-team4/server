package com.codeit.findex.indexinfo.dto;

import com.codeit.findex.global.common.SourceType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexInfoDto {
  private UUID id;
  private String indexClassification;
  private String indexName;
  private Integer employedItemsCount;
  private LocalDate basePointInTime;
  private BigDecimal baseIndex;
  private SourceType sourceType;
  private Boolean favorite;
}
