package com.codeit.findex.indexdata.repository;

import com.codeit.findex.indexdata.entity.IndexData;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.domain.Slice;

public interface IndexDataQueryRepository {

  Slice<IndexData> findAllSlice(UUID indexInfoId, LocalDate startDate,
      LocalDate endDate, UUID idAfter, String cursor, String sortField, String sortDirection,
      int size);

  Long countByFilters(UUID indexInfoId, LocalDate startDate, LocalDate endDate,
      UUID idAfter, String cursor, String sortField, String sortDirection, int size);
}
