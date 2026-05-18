package com.codeit.findex.indexdata.repository;

import com.codeit.findex.global.common.PeriodType;
import com.codeit.findex.indexdata.dto.IndexPerformanceDto;
import com.codeit.findex.indexdata.entity.IndexData;
import java.util.List;
import java.util.UUID;

public interface IndexDataQueryRepository {

  List<IndexData> findAllByFindexIdWithFindex(UUID findexId);
  List<IndexPerformanceDto> findPerformanceRanking(UUID indexInfoId, PeriodType periodType, Integer limit);
}
