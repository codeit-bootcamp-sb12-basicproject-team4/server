package com.codeit.findex.autointegration.controller;

import com.codeit.findex.autointegration.dto.AutoIntegrationResponseDto;
import com.codeit.findex.autointegration.service.AutoIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auto-integrations")
@Tag(name = "자동 연동 설정 API", description = "지수별 자동 연동 설정 관리 및 목록 조회 API")
public class AutoIntegrationController {

  private final AutoIntegrationService autoIntegrationService;

  @PutMapping("/{findexId}/active")
  @Operation(summary = "자동 연동 설정 활성화/비활성화 수정",
      description = "특정 지수의 자동 연동 여부(isActive)를 토글 수정합니다.")
  public ResponseEntity<AutoIntegrationResponseDto> updateActiveStatus(
      @PathVariable("findexId") UUID findexId,
      @RequestParam("isActive") Boolean isActive) {

    log.info("API 호출 - 자동 연동 설정 수정: 지수ID={}, 변경상태={}", findexId, isActive);
    AutoIntegrationResponseDto response
        = autoIntegrationService.updateActiveStatus(findexId, isActive);
    return ResponseEntity.ok(response);
  }


  @GetMapping
  @Operation(summary = "자동 연동 설정 목록 조회",
      description = "지수 ID, 활성화 여부 필터와 lastId 커서를 활용해 No-Offset 페이징 목록을 조회합니다.")
  public ResponseEntity<List<AutoIntegrationResponseDto>> getAutoIntegrations(
      @RequestParam(value = "findexId", required = false) UUID findexId,
      @RequestParam(value = "isActive", required = false) Boolean isActive,
      @Parameter(description = "이전 페이지의 마지막 요소 ID (커서)")
      @RequestParam(value = "lastId", required = false) String lastId,
      @RequestParam(value = "size", defaultValue = "10") int size) {

    log.info("API 호출 - 자동 연동 설정 목록 조회: findexId={}, isActive={}, lastId={}, size={}",
        findexId, isActive, lastId, size);

    List<AutoIntegrationResponseDto> responses
        = autoIntegrationService.getAutoIntegrations(findexId, isActive, lastId, size);
    return ResponseEntity.ok(responses);
  }
}