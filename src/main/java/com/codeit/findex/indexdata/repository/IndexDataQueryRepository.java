package com.codeit.findex.indexdata.repository;

import com.codeit.findex.global.common.UnitPeriodType;
import com.codeit.findex.indexdata.dto.IndexPerformanceDto;
import com.codeit.findex.indexdata.entity.IndexData;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Slice;


public interface IndexDataQueryRepository {

  List<IndexData> findAllByFindexIdWithFindex(UUID findexId);
  
  List<IndexPerformanceDto> findPerformanceRanking(UUID indexInfoId, UnitPeriodType unitPeriodType, Integer limit);

  Slice<IndexData> findAllWithCursor(UUID indexInfoId, LocalDate startDate,
      LocalDate endDate, UUID idAfter, String cursor, String sortField, String sortDirection,
      int size);

  Long countByFilters(UUID indexInfoId, LocalDate startDate, LocalDate endDate,
      UUID idAfter, String cursor);

  List<IndexData> findAllByCondition(UUID indexInfoId, LocalDate startDate, LocalDate endDate, String sortField, String sortDirection);
}
