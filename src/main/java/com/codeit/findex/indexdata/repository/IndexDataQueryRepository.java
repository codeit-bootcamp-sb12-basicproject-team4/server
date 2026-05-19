package com.codeit.findex.indexdata.repository;

import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexinfo.entity.Findex;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Slice;

public interface IndexDataQueryRepository {

  Slice<IndexData> findAllWithCursor(UUID indexInfoId, LocalDate startDate,
      LocalDate endDate, UUID idAfter, String cursor, String sortField, String sortDirection,
      int size);

  Long countByFilters(UUID indexInfoId, LocalDate startDate, LocalDate endDate,
      UUID idAfter, String cursor);

  List<IndexData> findAllByCondition(UUID indexInfoId, LocalDate startDate, LocalDate endDate, String sortField, String sortDirection);
}
