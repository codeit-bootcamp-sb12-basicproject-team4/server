package com.codeit.findex.indexdata.controller;

import com.codeit.findex.global.common.PeriodType;
import com.codeit.findex.indexdata.dto.IndexChartDto;
import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.dto.IndexDataUpdateRequest;
import com.codeit.findex.indexdata.dto.RankedIndexPerformanceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import com.codeit.findex.indexdata.dto.CursorPageResponseIndexDataDto;
import com.codeit.findex.indexdata.dto.IndexDataCreateRequest;
import com.codeit.findex.indexdata.dto.IndexDataDto;
import com.codeit.findex.indexdata.dto.IndexPerformanceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "지수 데이터 API", description = "지수 데이터 관리 API")
public interface IndexDataApi {

  @Operation(
      summary = "지수 차트 조회",
      description = "지수의 차트 데이터를 조회합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "차트 데이터 조회 성공.",
              content = @Content(schema = @Schema(implementation = IndexChartDto.class))),
          @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 기간 유형 등)"),
          @ApiResponse(responseCode = "404", description = "지수 정보를 찾을 수 없음"),
          @ApiResponse(responseCode = "500", description = "서버 오류")

      }
  )
  ResponseEntity<IndexChartDto> getIndexChart(
      @Parameter(description = "지수 정보 ID", example = "123e4567-e89b-12d3-a456-426614174000")
      @PathVariable UUID id,

      @Parameter(
          description = "차트 기간 유형 (DAILY, WEEKLY, MONTHLY)",
          schema = @Schema(allowableValues = {"MONTHLY", "QUARTERLY", "YEARLY"}, defaultValue = "DAILY")
      )
      @RequestParam(value = "periodType", defaultValue = "DAILY") PeriodType periodType
  );


  @Operation(
      summary = "지수 성과 랭킹 조회",
      description = "지수의 성과 분석 랭킹을 조회합니다.",
      responses = {
          @ApiResponse(responseCode = "200", description = "성과 랭킹 조회 성공",
              content = @Content(mediaType = "application/json",
                  schema = @Schema(implementation = RankedIndexPerformanceDto.class))),
          @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 기간 유형 등)"),
          @ApiResponse(responseCode = "500", description = "서버 오류")
      }
  )
  ResponseEntity<List<RankedIndexPerformanceDto>> getIndexPerformanceRank(
      @Parameter(description = "지수 정보 ID (선택 사항)", example = "123e4567-e89b-12d3-a456-426614174000")
      @RequestParam(value = "indexInfoId", required = false) UUID indexInfoId,

      @Parameter(
          description = "성과 기간 유형 (DAILY, WEEKLY, MONTHLY)",
          schema = @Schema(allowableValues = {"DAILY", "WEEKLY", "MONTHLY"}, defaultValue = "DAILY")
      )
      @RequestParam(value = "periodType", defaultValue = "DAILY") PeriodType periodType,

      @Parameter(description = "최대 랭킹 수", schema = @Schema(defaultValue = "10"))
      @RequestParam(value = "limit", defaultValue = "10") Integer limit
  );

  @Operation(
      summary = "지수 데이터 수정",
      description = "기존 지수 데이터를 수정합니다.",
      responses = {
    @ApiResponse(
        responseCode = "200",
        description = "지수 데이터 수정 성공",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = IndexDataDto.class))
    ),
    @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 데이터 값 등)"),
    @ApiResponse(responseCode = "404", description = "수정할 지수 데이터를 찾을 수 없음"),
    @ApiResponse(responseCode = "500", description = "서버 오류")
  }
  )
  ResponseEntity<IndexDataDto> updateIndexData(
      @Parameter(description = "지수 데이터 ID", example = "018f3a3b-1111-7000-8000-000000000011")
      @PathVariable UUID id,

      @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "지수 데이터 수정 요청 바디")
      IndexDataUpdateRequest request
  );

  @Operation(
      summary = "지수 데이터 삭제",
      description = "지정한 ID의 지수 데이터를 삭제합니다.",
      responses = {
          @ApiResponse(
              responseCode = "204",
              description = "지수 데이터 삭제 성공",
              content = @Content
          ),
          @ApiResponse(
              responseCode = "404", description = "삭제할 지수 데이터를 찾을 수 없음"
          ),
          @ApiResponse(
              responseCode = "500", description = "서버 오류"
          )
      }
  )
  ResponseEntity<Void> deleteIndexData(
      @Parameter(description = "지수 데이터 ID", required = true, example = "018f3a3b-1111-7000-8000-000000000011")
      @PathVariable UUID id
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

  @Operation(summary = "지수 데이터 등록", description = "새로운 지수 데이터를 등록합니다.", operationId = "createIndexData")
  @ApiResponses({
      @ApiResponse(responseCode = "201", description = "지수 데이터 생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 데이터 값 등)"),
      @ApiResponse(responseCode = "404", description = "참조하는 지수 정보를 찾을 수 없음"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  ResponseEntity<IndexDataDto> create(@RequestBody IndexDataCreateRequest request);

  @Operation(summary = "지수 데이터 상세 조회", description = "즐겨찾기로 등록된 지수들의 성과를 조회합니다.", operationId = "getFavoriteIndexPerformance")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "관심 지수 성과 조회 성공"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  ResponseEntity<List<IndexPerformanceDto>> getFavoriteIndexPerformance(
      @Parameter(name = "periodType", description = "성과 기간 유형 (DAILY, WEEKLY, MONTHLY)")
      @RequestParam(defaultValue = "DAILY", required = false) PeriodType periodType
  );

  @Operation(summary = "지수 데이터 CSV export", description = "지수 데이터를 CSV 파일로 export합니다.", operationId = "downloadIndexData")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "CSV 파일 생성 성공"),
      @ApiResponse(responseCode = "400", description = "잘못된 요청 (유효하지 않은 필터 값 등)"),
      @ApiResponse(responseCode = "500", description = "서버 오류")
  })
  ResponseEntity<byte[]> downloadIndexData(
      @Parameter(name = "indexInfoId", description = "지수 정보 ID")
      @RequestParam(required = false) UUID indexInfoId,
      @Parameter(name = "startDate", description = "시작 일자")
      @RequestParam(required = false) LocalDate startDate,
      @Parameter(name = "endDate", description = "종료 일자")
      @RequestParam(required = false) LocalDate endDate,
      @Parameter(name = "sortField",
          description = "정렬 필드 (baseDate, marketPrice, closingPrice, "
              + "highPrice, lowPrice, versus, fluctuationRate, "
              + "tradingQuantity, tradingPrice, marketTotalAmount)")
      @RequestParam(defaultValue = "baseDate", required = false) String sortField,
      @Parameter(name = "sortDirection", description = "정렬 방향 (asc, desc)")
      @RequestParam(defaultValue = "desc", required = false) String sortDirection
  );
}
