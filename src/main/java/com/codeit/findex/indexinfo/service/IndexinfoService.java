package com.codeit.findex.indexinfo.service;

import com.codeit.findex.indexinfo.entity.Findex;
import com.codeit.findex.indexinfo.repository.IndexinfoRepository;
import com.codeit.findex.integrationlog.dto.IndexResponse;
import com.codeit.findex.integrationlog.dto.OpenApiIndex;
import com.codeit.findex.integrationlog.entity.JobType;
import com.codeit.findex.integrationlog.entity.Result;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexinfoService {
  private final IndexinfoRepository indexinfoRepository;

  @Transactional
  public List<IndexResponse> processDatabaseUpdate(List<OpenApiIndex> rawData, String clientIp) {
    List<Findex> newEntities = rawData.stream()
        .filter(dto -> !indexinfoRepository.existsByIndexClassificationAndIndexName(
            dto.getIndexClassification(), dto.getIndexName()))
        .map(OpenApiIndex::toEntity)
        .collect(Collectors.toList());

    List<Findex> savedEntities = new ArrayList<>();
    if (!newEntities.isEmpty()) {
      log.info("신규 지수 정보 {}건 저장 시도", newEntities.size());
      savedEntities = indexinfoRepository.saveAll(newEntities);
    } else {
      log.info("새롭게 저장할 지수 정보가 없습니다.");
    }

    return savedEntities.stream()
        .map(entity -> IndexResponse.builder()
            .id(null) // 저장 후 생성된 PK
            .jobType(JobType.INDEX)
            .indexInfoId(entity.getId()) // 혹은 엔티티의 특정 ID 필드
            .targetDate(LocalDate.now().minusDays(5))
            .worker(clientIp)
            .jobTime(Instant.now())
            .result(Result.SUCCESS)
            .build())
        .collect(Collectors.toList());
  }
}
