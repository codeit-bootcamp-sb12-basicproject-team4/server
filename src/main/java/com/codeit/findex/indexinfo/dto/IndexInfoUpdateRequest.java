package com.codeit.findex.indexinfo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IndexInfoUpdateRequest {
  private Integer employedItemsCount;
  private LocalDate basePointInTime;
  private BigDecimal baseIndex;
  private Boolean favorite;
}
