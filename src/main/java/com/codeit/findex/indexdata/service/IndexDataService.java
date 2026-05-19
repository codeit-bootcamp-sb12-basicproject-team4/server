package com.codeit.findex.indexdata.service;

import com.codeit.findex.global.common.PeriodType;
import com.codeit.findex.indexdata.dto.CursorPageResponseIndexDataDto;
import com.codeit.findex.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.dto.IndexPerformanceDto;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface IndexDataService {
  CursorPageResponseIndexDataDto getIndexDataList(UUID indexInfoId,
      LocalDate startDate, LocalDate endDate, UUID idAfter, String cursor, String sortField,
      String sortDirection, int size);

  IndexDataDto create(@Valid IndexDataCreateRequest request);

  List<IndexPerformanceDto> getFavoriteIndexPerformance(PeriodType periodType);

  byte[] downloadIndexData(UUID indexInfoId, LocalDate startDate, LocalDate endDate, String sortField, String sortDirection);
}
