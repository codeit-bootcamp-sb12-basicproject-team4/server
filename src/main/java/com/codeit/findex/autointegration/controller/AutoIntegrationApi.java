package com.codeit.findex.autointegration.controller;

import com.codeit.findex.autointegration.dto.AutoIntegrationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "자동 연동 설정 API", description = "지수별 자동 연동 설정 관리 및 목록 조회 API")
public interface AutoIntegrationApi {

  @Operation(summary = "자동 연동 설정 활성화/비활성화 수정",
      description = "특정 지수의 자동 연동 여부(isActive)를 토글 수정합니다.")
  ResponseEntity<AutoIntegrationResponseDto> updateActiveStatus(
      UUID findexId,
      AutoIntegrationController.UpdateActiveStatusRequest request);

  @Operation(summary = "자동 연동 설정 목록 조회",
      description = "지수 ID, 활성화 여부 필터와 lastId 커서를 활용해 No-Offset 페이징 목록을 조회합니다.")
  ResponseEntity<List<AutoIntegrationResponseDto>> getAutoIntegrations(
      UUID findexId,
      Boolean isActive,
      @Parameter(description = "이전 페이지의 마지막 요소 ID (커서)") String lastId,
      int size);
}