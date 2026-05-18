package com.codeit.findex.indexdata.service;

import com.codeit.findex.global.common.PeriodType;
import com.codeit.findex.indexdata.dto.IndexChartDto;
import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.dto.IndexDataUpdateRequest;
import com.codeit.findex.indexdata.dto.RankedIndexPerformanceDto;
import java.util.List;
import java.util.UUID;

public interface IndexDataService {
  IndexChartDto getIndexChart(UUID indexInfoId, PeriodType periodType);
  List<RankedIndexPerformanceDto> getIndexPerformanceRank(UUID indexInfoId, PeriodType periodType, Integer limit);
  IndexDataDto updateIndexData(UUID id, IndexDataUpdateRequest request);
}
