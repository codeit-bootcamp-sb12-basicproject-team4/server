package com.codeit.findex.integrationlog.service;

import com.codeit.findex.integrationlog.dto.IndexResponse;
import com.codeit.findex.integrationlog.entity.IntegrationLog;
import com.codeit.findex.integrationlog.mapper.IntegrationLogMapper;
import com.codeit.findex.integrationlog.repository.IntegrationLogRepository;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationLogService {
  private final IntegrationLogRepository integrationLogRepository;
  private final IntegrationLogMapper integrationLogMapper;

  @Transactional
  public List<IndexResponse> createAll(List<IndexResponse> responses) {
    if (responses.isEmpty()) {
      log.info("저장할 통합 로그 데이터가 없습니다.");
      return Collections.emptyList();
    }

    List<IntegrationLog> logs = responses.stream()
        .map(integrationLogMapper::toEntity)
        .toList();

    List<IntegrationLog> savedLogs = integrationLogRepository.saveAll(logs);
    log.info("통합 로그 {}건 적재 완료", logs.size());

    IntStream.range(0, logs.size())
        .forEach(i -> {
          UUID generatedLogId = savedLogs.get(i).getId();
          responses.get(i).setId(generatedLogId);
        });
    return responses;
  }
}
