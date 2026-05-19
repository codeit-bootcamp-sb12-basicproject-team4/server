package com.codeit.findex.integrationlog.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.findex.indexdata.entity.IndexData;
import com.codeit.findex.indexdata.repository.IndexDataRepository;
import com.codeit.findex.indexinfo.entity.Findex;
import com.codeit.findex.indexinfo.repository.IndexinfoRepository;
import com.codeit.findex.integrationlog.dto.IndexResponse;
import com.codeit.findex.integrationlog.dto.OpenApiIndex;
import com.codeit.findex.integrationlog.entity.JobType;
import com.codeit.findex.integrationlog.entity.Result;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class IndexDataSyncServiceTest {

  @Mock
  private IndexDataRepository indexDataRepository;

  @Mock
  private IndexinfoRepository indexinfoRepository;

  @InjectMocks
  private IndexDataSyncService indexDataSyncService;

  @Test
  void savesOpenApiIndexDataAndReturnsSuccessLogResponse() {
    UUID indexInfoId = UUID.randomUUID();
    LocalDate targetDate = LocalDate.of(2026, 5, 14);
    Findex findex = Findex.builder()
        .id(indexInfoId)
        .indexClassification("KOSPI")
        .indexName("KOSPI 200")
        .itemsCount(200)
        .basePntm(LocalDate.of(1990, 1, 3))
        .baseIndex(BigDecimal.valueOf(100))
        .favorite(false)
        .build();
    OpenApiIndex raw = new OpenApiIndex();
    raw.setIndexClassification("KOSPI");
    raw.setIndexName("KOSPI 200");
    raw.setBaseDate("20260514");
    raw.setMarketPrice(BigDecimal.valueOf(100.12));
    raw.setClosePrice(BigDecimal.valueOf(101.34));
    raw.setHighPrice(BigDecimal.valueOf(102.56));
    raw.setLowPrice(BigDecimal.valueOf(99.78));
    raw.setVersus(BigDecimal.valueOf(1.23));
    raw.setFluctuationRate(BigDecimal.valueOf(1.25));
    raw.setTradingQuantity(12345L);
    raw.setTradingPrice(98765L);
    raw.setMarketTotalamount(55555L);

    when(indexinfoRepository.findAllById(List.of(indexInfoId))).thenReturn(List.of(findex));
    when(indexDataRepository.findByFindexIdAndBaseDate(indexInfoId, targetDate))
        .thenReturn(Optional.empty());
    when(indexDataRepository.save(any(IndexData.class))).thenAnswer(invocation -> invocation.getArgument(0));

    List<IndexResponse> responses = indexDataSyncService.processDatabaseUpdate(
        List.of(raw),
        List.of(indexInfoId),
        targetDate,
        "127.0.0.1"
    );

    assertThat(responses).hasSize(1);
    assertThat(responses.get(0).getJobType()).isEqualTo(JobType.DATA);
    assertThat(responses.get(0).getIndexInfoId()).isEqualTo(indexInfoId);
    assertThat(responses.get(0).getTargetDate()).isEqualTo(targetDate);
    assertThat(responses.get(0).getWorker()).isEqualTo("127.0.0.1");
    assertThat(responses.get(0).getResult()).isEqualTo(Result.SUCCESS);

    ArgumentCaptor<IndexData> captor = ArgumentCaptor.forClass(IndexData.class);
    verify(indexDataRepository).save(captor.capture());
    assertThat(captor.getValue().getFindex()).isSameAs(findex);
    assertThat(captor.getValue().getBaseDate()).isEqualTo(targetDate);
    assertThat(captor.getValue().getClosePrice()).isEqualByComparingTo("101.34");
  }
}
