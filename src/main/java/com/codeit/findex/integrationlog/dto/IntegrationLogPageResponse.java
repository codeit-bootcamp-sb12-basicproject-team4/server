package com.codeit.findex.integrationlog.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegrationLogPageResponse {

  private List<IndexResponse> content;

  private String nextCursor;

  private String nextIdAfter;

  private int size;

  private long totalElements;

  private boolean hasNext;
}
