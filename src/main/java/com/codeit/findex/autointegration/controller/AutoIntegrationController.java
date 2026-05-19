package com.codeit.findex.autointegration.controller;

import com.codeit.findex.autointegration.dto.AutoIntegrationResponseDto;
import com.codeit.findex.autointegration.service.AutoIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auto-integrations")
public class AutoIntegrationController implements AutoIntegrationApi{

  private final AutoIntegrationService autoIntegrationService;

  @Override
  @PatchMapping("/{findexId}")
  public ResponseEntity<AutoIntegrationResponseDto> updateActiveStatus(
      @PathVariable("findexId") UUID findexId,
      @RequestBody UpdateActiveStatusRequest request) {

    log.info("API 호출 - 자동 연동 설정 수정: 지수ID={}, 변경상태={}", findexId, request.getIsActive());
    AutoIntegrationResponseDto response
        = autoIntegrationService.updateActiveStatus(findexId, request.getIsActive());
    return ResponseEntity.ok(response);
  }


  @Override
  @GetMapping
  public ResponseEntity<List<AutoIntegrationResponseDto>> getAutoIntegrations(
      @RequestParam(value = "findexId", required = false) UUID findexId,
      @RequestParam(value = "isActive", required = false) Boolean isActive,
      @RequestParam(value = "lastId", required = false) String lastId,
      @RequestParam(value = "size", defaultValue = "10") int size) {

    log.info("API 호출 - 자동 연동 설정 목록 조회: findexId={}, isActive={}, lastId={}, size={}",
        findexId, isActive, lastId, size);

    List<AutoIntegrationResponseDto> responses
        = autoIntegrationService.getAutoIntegrations(findexId, isActive, lastId, size);
    return ResponseEntity.ok(responses);
  }

  @Getter
  @NoArgsConstructor
  public static class UpdateActiveStatusRequest {
    private Boolean isActive;
  }
}