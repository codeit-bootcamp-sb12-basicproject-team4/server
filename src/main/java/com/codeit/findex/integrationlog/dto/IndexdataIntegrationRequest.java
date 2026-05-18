package com.codeit.findex.integrationlog.dto;

import java.time.LocalDate;
import java.util.List;
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
public class IndexdataIntegrationRequest {

  private List<UUID> indexInfoIds;

  private LocalDate baseDateFrom;

  private LocalDate baseDateTo;

}
