package com.codeit.findex.indexinfo.service;

import com.codeit.findex.indexinfo.entity.Findex;
import com.codeit.findex.indexinfo.repository.IndexinfoRepository;
import com.codeit.findex.integrationlog.dto.IndexResponse;
import com.codeit.findex.integrationlog.dto.OpenApiIndex;
import com.codeit.findex.integrationlog.entity.JobType;
import com.codeit.findex.integrationlog.entity.Result;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
  public List<IndexResponse> processDatabaseUpdate(List<OpenApiIndex> rawData, String worker) {
    List<IndexResponse> allResponses = new ArrayList<>();
    List<IndexResponse> pendingResponses = new ArrayList<>();
    List<Findex> toSaveEntities = new ArrayList<>();

    for (OpenApiIndex dto : rawData) {
      Optional<Findex> existing = indexinfoRepository.findByIndexClassificationAndIndexName(
          dto.getIndexClassification(), dto.getIndexName());

      IndexResponse response = IndexResponse.builder()
          .jobType(JobType.INDEX)
          .targetDate(LocalDate.now().minusDays(5))
          .worker(worker)
          .jobTime(Instant.now())
          .result(Result.SUCCESS)
          .build();

      if (existing.isPresent()) {
        response.setIndexInfoId(existing.get().getId());
      } else {
        Findex newEntity = dto.toEntity();
        toSaveEntities.add(newEntity);
        pendingResponses.add(response);
      }
      allResponses.add(response);
    }

    if (!toSaveEntities.isEmpty()) {
      List<Findex> savedEntities = indexinfoRepository.saveAll(toSaveEntities);

      for (int i = 0; i < savedEntities.size(); i++) {
        pendingResponses.get(i).setIndexInfoId(savedEntities.get(i).getId());
      }
    }
    return allResponses;
  }
}
