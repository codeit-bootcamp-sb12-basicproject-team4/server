package com.codeit.findex.indexdata.service;

import com.codeit.findex.indexdata.dto.CursorPageResponseIndexDataDto;
import java.time.LocalDate;
import java.util.UUID;

public interface IndexDataService {
  CursorPageResponseIndexDataDto getIndexDataList(UUID indexInfoId,
      LocalDate startDate, LocalDate endDate, UUID idAfter, String cursor, String sortField,
      String sortDirection, int size);
}
