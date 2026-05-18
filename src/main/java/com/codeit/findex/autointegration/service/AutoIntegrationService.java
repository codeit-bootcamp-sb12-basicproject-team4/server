package com.codeit.findex.autointegration.service;

import com.codeit.findex.autointegration.dto.AutoIntegrationResponseDto;
import com.codeit.findex.autointegration.entity.AutoIntegration;
import com.codeit.findex.autointegration.repository.AutoIntegrationRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AutoIntegrationService {

  private final AutoIntegrationRepository autoIntegrationRepository;

  @Transactional
  public AutoIntegrationResponseDto updateActiveStatus(UUID findexId, Boolean isActive) {
    log.info("자동 연동 설정 수정 요청 - 지수 ID: {}, 변경 상태: {}", findexId, isActive);


    AutoIntegration autoIntegration = autoIntegrationRepository.findById(findexId)
        .orElseThrow(() ->
            new EntityNotFoundException("해당 지수의 연동 설정이 존재하지 않습니다. ID: " + findexId));

    autoIntegration.updateActive(isActive);

    return new AutoIntegrationResponseDto(autoIntegration);
  }


  public List<AutoIntegrationResponseDto> getAutoIntegrations
      (UUID findexId, Boolean isActive, String lastId, int size) {
    log.info("자동 연동 설정 목록 조회 - 필터(지수ID: {}, 활성화여부: {}), 커서ID: {}, 페이지 크기: {}",
              findexId, isActive, lastId, size);

    Pageable pageable = PageRequest.of(0, size);


    List<AutoIntegration> results
        = autoIntegrationRepository.findByFiltersAndCursor(findexId, isActive, lastId, pageable);

    return results.stream()
        .map(AutoIntegrationResponseDto::new)
        .collect(Collectors.toList());
  }
}