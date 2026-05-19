package com.codeit.findex.indexdata.repository;

import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexinfo.entity.Findex;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Slice;
import com.codeit.findex.global.common.PeriodType;
import com.codeit.findex.indexdata.dto.IndexPerformanceDto;
import com.codeit.findex.indexdata.entity.IndexData;
import java.util.List;
import java.util.UUID;

public interface IndexDataQueryRepository {

  List<IndexData> findAllByFindexIdWithFindex(UUID findexId);
  
  List<IndexPerformanceDto> findPerformanceRanking(UUID indexInfoId, PeriodType periodType, Integer limit);
  
  Slice<IndexData> findAllWithCursor(UUID indexInfoId, LocalDate startDate,
      LocalDate endDate, UUID idAfter, String cursor, String sortField, String sortDirection,
      int size);

  Long countByFilters(UUID indexInfoId, LocalDate startDate, LocalDate endDate,
      UUID idAfter, String cursor);

  List<IndexData> findAllByCondition(UUID indexInfoId, LocalDate startDate, LocalDate endDate, String sortField, String sortDirection);
}
