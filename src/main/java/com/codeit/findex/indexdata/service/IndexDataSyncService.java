package com.codeit.findex.indexdata.service;

import com.codeit.findex.global.common.SourceType;
import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexdata.repository.IndexDataRepository;
import com.codeit.findex.indexinfo.entity.Findex;
import com.codeit.findex.indexinfo.repository.IndexinfoRepository;
import com.codeit.findex.integrationlog.dto.IndexResponse;
import com.codeit.findex.integrationlog.dto.OpenApiIndex;
import com.codeit.findex.integrationlog.entity.JobType;
import com.codeit.findex.integrationlog.entity.Result;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexDataSyncService {

  private static final DateTimeFormatter OPEN_API_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

  private final IndexDataRepository indexDataRepository;
  private final IndexinfoRepository indexinfoRepository;

  @Transactional
  public List<IndexResponse> processDatabaseUpdate(
      List<OpenApiIndex> rawData,
      List<UUID> indexInfoIds,
      LocalDate targetDate,
      String worker
  ) {
    List<Findex> targets = findTargets(indexInfoIds);
    log.info("지수 데이터 DB 반영 시작 - targetDate: {}, targetCount: {}, rawDataCount: {}, worker: {}",
        targetDate, targets.size(), rawData.size(), worker);

    Map<String, OpenApiIndex> rawDataByIndex = rawData.stream()
        .collect(Collectors.toMap(
            dto -> keyOf(dto.getIndexClassification(), dto.getIndexName()),
            Function.identity(),
            (left, right) -> left,
            HashMap::new
        ));

    List<IndexResponse> responses = targets.stream()
        .map(findex -> syncOneIndex(findex, rawDataByIndex.get(keyOf(findex)), targetDate, worker))
        .toList();
    log.info("지수 데이터 DB 반영 완료 - targetDate: {}, responseCount: {}", targetDate, responses.size());
    return responses;
  }

  private List<Findex> findTargets(List<UUID> indexInfoIds) {
    if (indexInfoIds == null || indexInfoIds.isEmpty()) {
      List<Findex> allTargets = indexinfoRepository.findAll();
      log.info("지수 데이터 연동 대상 전체 조회 - targetCount: {}", allTargets.size());
      return allTargets;
    }

    List<Findex> targets = indexinfoRepository.findAllById(indexInfoIds);
    if (targets.size() != indexInfoIds.size()) {
      log.warn("지수 데이터 연동 대상 조회 실패 - requestedIds: {}, foundCount: {}",
          indexInfoIds, targets.size());
      throw new NoSuchElementException("Index info not found.");
    }
    log.info("지수 데이터 연동 대상 조회 완료 - requestedCount: {}, foundCount: {}",
        indexInfoIds.size(), targets.size());
    return targets;
  }

  private IndexResponse syncOneIndex(
      Findex findex,
      OpenApiIndex rawData,
      LocalDate targetDate,
      String worker
  ) {
    Result result = Result.FAIL;
    if (rawData != null) {
      indexDataRepository.findByFindexIdAndBaseDate(findex.getId(), targetDate)
          .ifPresentOrElse(
              existing -> {
                update(existing, rawData);
                log.info("지수 데이터 수정 완료 - indexInfoId: {}, indexName: {}, targetDate: {}",
                    findex.getId(), findex.getIndexName(), targetDate);
              },
              () -> {
                indexDataRepository.save(toEntity(findex, rawData));
                log.info("지수 데이터 등록 완료 - indexInfoId: {}, indexName: {}, targetDate: {}",
                    findex.getId(), findex.getIndexName(), targetDate);
              }
          );
      result = Result.SUCCESS;
    } else {
      log.warn("OpenAPI 응답에서 대상 지수를 찾지 못했습니다 - indexInfoId: {}, indexName: {}, targetDate: {}",
          findex.getId(), findex.getIndexName(), targetDate);
    }

    return IndexResponse.builder()
        .jobType(JobType.DATA)
        .indexInfoId(findex.getId())
        .targetDate(targetDate)
        .worker(worker)
        .jobTime(Instant.now())
        .result(result)
        .build();
  }

  private void update(IndexData existing, OpenApiIndex rawData) {
    existing.updateFromOpenApi(
        rawData.getMarketPrice(),
        rawData.getClosePrice(),
        rawData.getHighPrice(),
        rawData.getLowPrice(),
        rawData.getVersus(),
        rawData.getFluctuationRate(),
        rawData.getTradingQuantity(),
        rawData.getTradingPrice(),
        rawData.getMarketTotalamount()
    );
  }

  private IndexData toEntity(Findex findex, OpenApiIndex rawData) {
    return IndexData.builder()
        .findex(findex)
        .baseDate(LocalDate.parse(rawData.getBaseDate(), OPEN_API_DATE_FORMATTER))
        .sourceType(SourceType.OPEN_API)
        .marketPrice(rawData.getMarketPrice())
        .closePrice(rawData.getClosePrice())
        .highPrice(rawData.getHighPrice())
        .lowPrice(rawData.getLowPrice())
        .versus(rawData.getVersus())
        .fluctuationRate(rawData.getFluctuationRate())
        .tradingQuantity(rawData.getTradingQuantity())
        .tradingPrice(rawData.getTradingPrice())
        .marketTotalamount(rawData.getMarketTotalamount())
        .build();
  }

  private String keyOf(Findex findex) {
    return keyOf(findex.getIndexClassification(), findex.getIndexName());
  }

  private String keyOf(String indexClassification, String indexName) {
    return indexClassification + "\n" + indexName;
  }
}
