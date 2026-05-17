package com.codeit.findex.indexdata.controller;

import com.codeit.findex.indexdata.dto.CursorPageResponseIndexDataDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "지수 데이터 API", description = "지수 데이터 관리 API")
public interface IndexDataApi {

  @Operation(summary = "지수 데이터 목록 조회",
      description = "지수 데이터 목록을 조회합니다. 필터링, 정렬, 커서 기반 페이지네이션을 지원합니다.",
      operationId = "getIndexDataList")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "지수 데이터 목록 조회 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  ResponseEntity<CursorPageResponseIndexDataDto> getIndexDataList(
      @Parameter(name = "indexInfoId", description = "지수 정보 ID")
      @RequestParam(required = false) UUID indexInfoId,
      @Parameter(name = "startDate", description = "시작 일자")
      @RequestParam(required = false) LocalDate startDate,
      @Parameter(name = "endDate", description = "종료 일자")
      @RequestParam(required = false) LocalDate endDate,
      @Parameter(name = "idAfter", description = "이전 페이지 마지막 요소 ID")
      @RequestParam(required = false) UUID idAfter,
      @Parameter(name = "cursor", description = "커서 (다음 페이지 시작점)")
      @RequestParam(required = false) String cursor,
      @Parameter(name = "sortField",
          description = "정렬 필드 (baseDate, marketPrice, closingPrice, "
              + "highPrice, lowPrice, versus, fluctuationRate, "
              + "tradingQuantity, tradingPrice, marketTotalAmount)")
      @RequestParam(defaultValue = "baseDate", required = false) String sortField,
      @Parameter(name = "sortDirection", description = "정렬 방향 (asc, desc)")
      @RequestParam(defaultValue = "desc", required = false) String sortDirection,
      @Parameter(name = "size", description = "페이지 크기")
      @RequestParam(defaultValue = "10", required = false) int size);
}
