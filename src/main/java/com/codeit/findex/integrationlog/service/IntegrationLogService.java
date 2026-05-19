package com.codeit.findex.integrationlog.service;

import com.codeit.findex.integrationlog.dto.IndexResponse;
import com.codeit.findex.integrationlog.dto.IntegrationLogPageResponse;
import com.codeit.findex.integrationlog.dto.IntegrationLogSearchRequest;
import com.codeit.findex.integrationlog.entity.IntegrationLog;
import com.codeit.findex.integrationlog.mapper.IntegrationLogMapper;
import com.codeit.findex.integrationlog.repository.IntegrationLogRepository;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationLogService {
  private final IntegrationLogRepository integrationLogRepository;
  private final IntegrationLogMapper integrationLogMapper;

  @Transactional(readOnly = true)
  public IntegrationLogPageResponse search(IntegrationLogSearchRequest request) {
    IntegrationLogSearchRequest normalizedRequest = normalize(request);
    PageRequest pageable = PageRequest.of(
        0,
        normalizedRequest.getSize(),
        Sort.by(toSortDirection(normalizedRequest.getSortDirection()), normalizedRequest.getSortField())
    );

    Page<IntegrationLog> page = integrationLogRepository.searchPageSorted(normalizedRequest, pageable);
    List<IndexResponse> content = page.getContent().stream()
        .map(this::toResponse)
        .toList();
    boolean hasNext = page.hasNext();
    String nextIdAfter = findNextIdAfter(content, hasNext);

    return IntegrationLogPageResponse.builder()
        .content(content)
        .nextCursor(nextIdAfter == null ? null : encodeCursor(nextIdAfter))
        .nextIdAfter(nextIdAfter)
        .size(normalizedRequest.getSize())
        .totalElements(page.getTotalElements())
        .hasNext(hasNext)
        .build();
  }

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

  private IntegrationLogSearchRequest normalize(IntegrationLogSearchRequest request) {
    IntegrationLogSearchRequest normalized = request == null ? new IntegrationLogSearchRequest() : request;
    normalized.setJobType(normalizeJobType(normalized.getJobType()));
    normalized.setStatus(normalizeStatus(normalized.getStatus()));
    normalized.setSortField(defaultIfBlank(normalized.getSortField(), "jobTime"));
    normalized.setSortDirection(defaultIfBlank(normalized.getSortDirection(), "desc"));
    normalized.setSize(normalized.getSize() == null ? 10 : normalized.getSize());
    validate(normalized);
    return normalized;
  }

  private void validate(IntegrationLogSearchRequest request) {
    if (!List.of("targetDate", "jobTime").contains(request.getSortField())) {
      throw new IllegalArgumentException("정렬 필드는 targetDate 또는 jobTime만 가능합니다.");
    }
    String sortDirection = request.getSortDirection().toLowerCase();
    if (!List.of("asc", "desc").contains(sortDirection)) {
      throw new IllegalArgumentException("정렬 방향은 asc 또는 desc만 가능합니다.");
    }
    if (request.getSize() <= 0) {
      throw new IllegalArgumentException("페이지 크기는 1 이상이어야 합니다.");
    }
    if (request.getBaseDateFrom() != null && request.getBaseDateTo() != null
        && request.getBaseDateFrom().isAfter(request.getBaseDateTo())) {
      throw new IllegalArgumentException("대상 날짜 시작일은 종료일보다 이후일 수 없습니다.");
    }
    if (request.getJobTimeFrom() != null && request.getJobTimeTo() != null
        && request.getJobTimeFrom().isAfter(request.getJobTimeTo())) {
      throw new IllegalArgumentException("작업 일시 시작값은 종료값보다 이후일 수 없습니다.");
    }
  }

  private String normalizeJobType(String jobType) {
    if (jobType == null || jobType.isBlank()) {
      return null;
    }
    return switch (jobType.trim().toUpperCase()) {
      case "INDEX_INFO", "INDEX" -> "INDEX";
      case "INDEX_DATA", "DATA" -> "DATA";
      default -> throw new IllegalArgumentException("연동 작업 유형은 INDEX_INFO 또는 INDEX_DATA만 가능합니다.");
    };
  }

  private String normalizeStatus(String status) {
    if (status == null || status.isBlank()) {
      return null;
    }
    return switch (status.trim().toUpperCase()) {
      case "SUCCESS" -> "SUCCESS";
      case "FAILED", "FAIL" -> "FAIL";
      default -> throw new IllegalArgumentException("작업 상태는 SUCCESS 또는 FAILED만 가능합니다.");
    };
  }

  private String defaultIfBlank(String value, String defaultValue) {
    return value == null || value.isBlank() ? defaultValue : value;
  }

  private Sort.Direction toSortDirection(String sortDirection) {
    return "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
  }

  private IndexResponse toResponse(IntegrationLog log) {
    return IndexResponse.builder()
        .id(log.getId())
        .jobType(log.getJobType())
        .indexInfoId(log.getFindex().getId())
        .targetDate(log.getTargetDate())
        .worker(log.getWorker())
        .jobTime(log.getJobTime())
        .result(log.getResult())
        .build();
  }

  private String findNextIdAfter(List<IndexResponse> content, boolean hasNext) {
    if (!hasNext || content.isEmpty()) {
      return null;
    }
    return content.get(content.size() - 1).getId().toString();
  }

  private String encodeCursor(String id) {
    String cursorJson = "{\"id\":\"" + id + "\"}";
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(cursorJson.getBytes(StandardCharsets.UTF_8));
  }
}
