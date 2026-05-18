package com.codeit.findex.indexdata.controller;

import com.codeit.findex.indexdata.dto.IndexChartDto;
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
      @RequestParam(value = "periodType", defaultValue = "DAILY") String periodType
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
      @RequestParam(value = "periodType", defaultValue = "DAILY") String periodType,

      @Parameter(description = "최대 랭킹 수", schema = @Schema(defaultValue = "10"))
      @RequestParam(value = "limit", defaultValue = "10") int limit
  );
}
