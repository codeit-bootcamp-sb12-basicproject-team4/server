package com.codeit.findex.integrationlog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.findex.global.common.SourceType;
import com.codeit.findex.indexinfo.entity.Findex;
import com.codeit.findex.integrationlog.dto.IntegrationLogPageResponse;
import com.codeit.findex.integrationlog.dto.IntegrationLogSearchRequest;
import com.codeit.findex.integrationlog.entity.IntegrationLog;
import com.codeit.findex.integrationlog.entity.JobType;
import com.codeit.findex.integrationlog.entity.Result;
import com.codeit.findex.integrationlog.mapper.IntegrationLogMapper;
import com.codeit.findex.integrationlog.repository.IntegrationLogRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class IntegrationLogServiceTest {

  @Mock
  private IntegrationLogRepository integrationLogRepository;

  @Mock
  private IntegrationLogMapper integrationLogMapper;

  @InjectMocks
  private IntegrationLogService integrationLogService;

  @Test
  void searchesIntegrationLogsWithNormalizedFiltersAndPageResponse() {
    UUID logId = UUID.randomUUID();
    UUID indexInfoId = UUID.randomUUID();
    LocalDate targetDate = LocalDate.of(2026, 5, 14);
    Instant jobTime = Instant.parse("2026-05-14T09:00:00Z");
    Findex findex = Findex.builder()
        .id(indexInfoId)
        .indexClassification("KOSPI")
        .indexName("KOSPI 200")
        .basePntm(LocalDate.of(1990, 1, 3))
        .baseIndex(BigDecimal.valueOf(100))
        .sourceType(SourceType.OPEN_API)
        .favorite(false)
        .build();
    IntegrationLog log = IntegrationLog.builder()
        .id(logId)
        .jobType(JobType.DATA)
        .targetDate(targetDate)
        .worker("127.0.0.1")
        .jobTime(jobTime)
        .result(Result.FAIL)
        .findex(findex)
        .build();
    IntegrationLogSearchRequest request = IntegrationLogSearchRequest.builder()
        .jobType("INDEX_DATA")
        .status("FAILED")
        .sortField("jobTime")
        .sortDirection("desc")
        .size(1)
        .build();

    when(integrationLogRepository.searchPageSorted(any(), any()))
        .thenReturn(new PageImpl<>(List.of(log), PageRequest.of(0, 1), 2));

    IntegrationLogPageResponse response = integrationLogService.search(request);

    assertThat(response.getContent()).hasSize(1);
    assertThat(response.getContent().get(0).getId()).isEqualTo(logId);
    assertThat(response.getContent().get(0).getJobType()).isEqualTo(JobType.DATA);
    assertThat(response.getContent().get(0).getIndexInfoId()).isEqualTo(indexInfoId);
    assertThat(response.getContent().get(0).getTargetDate()).isEqualTo(targetDate);
    assertThat(response.getContent().get(0).getWorker()).isEqualTo("127.0.0.1");
    assertThat(response.getContent().get(0).getJobTime()).isEqualTo(jobTime);
    assertThat(response.getContent().get(0).getResult()).isEqualTo(Result.FAIL);
    assertThat(response.getSize()).isEqualTo(1);
    assertThat(response.getTotalElements()).isEqualTo(2);
    assertThat(response.isHasNext()).isTrue();
    assertThat(response.getNextIdAfter()).isEqualTo(logId.toString());
    assertThat(response.getNextCursor()).isNotBlank();

    ArgumentCaptor<IntegrationLogSearchRequest> requestCaptor =
        ArgumentCaptor.forClass(IntegrationLogSearchRequest.class);
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(integrationLogRepository).searchPageSorted(requestCaptor.capture(), pageableCaptor.capture());
    assertThat(requestCaptor.getValue().getJobType()).isEqualTo("DATA");
    assertThat(requestCaptor.getValue().getStatus()).isEqualTo("FAIL");
    assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(1);
    assertThat(pageableCaptor.getValue().getSort().getOrderFor("jobTime").isDescending()).isTrue();
  }
}
