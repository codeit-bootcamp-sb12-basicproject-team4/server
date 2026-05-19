package com.codeit.findex.indexdata.dto;

import com.codeit.findex.global.common.UnitPeriodType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.UUID;

@Schema(description = "지수 차트 데이터 DTO")
public record IndexChartDto(
    @Schema(description = "지수 정보 ID", example = "")
    UUID indexInfoID,
    @Schema(description = "지수 분류명", example = "KOSPI시리즈")
    String indexClassification,
    @Schema(description = "지수명", example = "IT 서비스")
    String indexName,
    @Schema(description = "차트 기간 유형 (DAILY, WEEKLY, MONTHLY)",
        allowableValues = {"MONTHLY", "QUARTERLY", "YEARLY"},
        example = "DAILY")
    UnitPeriodType periodType,
    @Schema(description = "차트 데이터 목록")
    List<ChartDataPoint> dataPoints,
    @Schema(description = "5일 이동평균선 데이터 목록")
    List<ChartDataPoint> ma5DataPoints,
    @Schema(description = "20일 이동평균선 데이터 목록")
    List<ChartDataPoint> ma20DataPoints

) {

}
