package com.codeit.findex.integrationlog.service;

import com.codeit.findex.indexinfo.service.IndexinfoService;
import com.codeit.findex.integrationlog.dto.IndexResponse;
import com.codeit.findex.integrationlog.dto.OpenApiIndex;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IntegrationFacade {
  private final OpenApiClient openApiClient;
  private final IndexinfoService indexinfoService;
  private final IntegrationLogService integrationLogService;

  public List<IndexResponse> syncIndexInfo(String clientIp) {
    List<OpenApiIndex> rawData = openApiClient.fetchIndexInfo(LocalDate.now().minusDays(5).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    if (rawData.isEmpty()) {
      return Collections.emptyList();
    }
    return integrationLogService.createAll(indexinfoService.processDatabaseUpdate(rawData, clientIp));
  }
}
