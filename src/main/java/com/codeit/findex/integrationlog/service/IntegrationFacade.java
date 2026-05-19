package com.codeit.findex.integrationlog.service;

import com.codeit.findex.indexinfo.service.IndexinfoService;
import com.codeit.findex.integrationlog.dto.IndexResponse;
import com.codeit.findex.integrationlog.dto.IndexdataIntegrationRequest;
import com.codeit.findex.integrationlog.dto.OpenApiIndex;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationFacade {
  private final OpenApiClient openApiClient;
  private final IndexinfoService indexinfoService;
  private final IndexDataSyncService indexDataSyncService;
  private final IntegrationLogService integrationLogService;

  public List<IndexResponse> syncIndexInfo(String worker) {
    List<OpenApiIndex> rawData = openApiClient.fetchIndexInfo(LocalDate.now().minusDays(5).format(DateTimeFormatter.ofPattern("yyyyMMdd")));
    if (rawData.isEmpty()) {
      return Collections.emptyList();
    }
    return integrationLogService.createAll(indexinfoService.processDatabaseUpdate(rawData, worker));
  }

  public List<IndexResponse> syncIndexData(IndexdataIntegrationRequest request, String worker) {
    validateRequest(request);

    log.info("지수 데이터 연동 시작 - worker: {}, indexInfoIds: {}, baseDateFrom: {}, baseDateTo: {}",
        worker, request.getIndexInfoIds(), request.getBaseDateFrom(), request.getBaseDateTo());

    List<IndexResponse> responses = new ArrayList<>();
    LocalDate current = request.getBaseDateFrom();
    while (!current.isAfter(request.getBaseDateTo())) {
      log.info("지수 데이터 OpenAPI 조회 시작 - targetDate: {}", current);
      List<OpenApiIndex> rawData = openApiClient.fetchIndexData(current.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
      log.info("지수 데이터 OpenAPI 조회 완료 - targetDate: {}, rawDataCount: {}", current, rawData.size());
      responses.addAll(indexDataSyncService.processDatabaseUpdate(
          rawData,
          request.getIndexInfoIds(),
          current,
          worker
      ));
      current = current.plusDays(1);
    }

    List<IndexResponse> savedResponses = integrationLogService.createAll(responses);
    log.info("지수 데이터 연동 완료 - worker: {}, resultCount: {}", worker, savedResponses.size());
    return savedResponses;
  }

  private void validateRequest(IndexdataIntegrationRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("요청 본문은 필수입니다.");
    }
    if (request.getBaseDateFrom() == null || request.getBaseDateTo() == null) {
      throw new IllegalArgumentException("대상 날짜는 필수입니다.");
    }
    if (request.getBaseDateFrom().isAfter(request.getBaseDateTo())) {
      throw new IllegalArgumentException("시작 날짜는 종료 날짜보다 이후일 수 없습니다.");
    }
  }
}
