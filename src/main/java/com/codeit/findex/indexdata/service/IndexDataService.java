package com.codeit.findex.indexdata.service;

import com.codeit.findex.indexdata.dto.IndexChartDto;
import com.codeit.findex.indexdata.dto.RankedIndexPerformanceDto;
import java.util.List;
import java.util.UUID;

public interface IndexDataService {
  IndexChartDto getIndexChart(UUID indexInfoId, String periodType);
  List<RankedIndexPerformanceDto> getIndexPerformanceRank(UUID indexInfoId, String periodType, int limit);
}
