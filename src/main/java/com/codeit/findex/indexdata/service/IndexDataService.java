package com.codeit.findex.indexdata.service;

import com.codeit.findex.indexdata.dto.CursorPageResponseIndexDataDto;
import com.codeit.findex.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.indexdata.dto.IndexDataDto;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.UUID;

public interface IndexDataService {
  CursorPageResponseIndexDataDto findIndexData(UUID indexInfoId,
      LocalDate startDate, LocalDate endDate, UUID idAfter, String cursor, String sortField,
      String sortDirection, int size);

  IndexDataDto create(@Valid IndexDataCreateRequest request);
}
