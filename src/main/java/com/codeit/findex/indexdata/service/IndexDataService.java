package com.codeit.findex.indexdata.service;

import com.codeit.findex.indexdata.dto.IndexChartDto;
import java.util.UUID;

public interface IndexDataService {
  IndexChartDto getIndexChart(UUID indexInfoId, String periodType);
}
