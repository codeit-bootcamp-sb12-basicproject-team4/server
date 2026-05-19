package com.codeit.findex.indexdata.dto;

import com.codeit.findex.global.common.SourceType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Schema(description = "지수 데이터 DTO")
public record IndexDataDto(
    @Schema(description = "지수 데이터 ID", example = "018f3a3a-1111-7000-8000-000000000001")
    UUID id,
    @Schema(description = "지수 정보 ID", example = "018f3a3a-1111-7000-8000-000000000001")
    UUID indexInfoId,
    @Schema(description = "기준 일자", example = "2026-01-01")
    LocalDate baseDate,
    @Schema(description = "출처 (사용자, Open API)", example = "OPEN_API")
    SourceType sourceType,
    @Schema(description = "시가", example = "2800.25")
    BigDecimal marketPrice,
    @Schema(description = "종가", example = "2850.75")
    BigDecimal closingPrice,
    @Schema(description = "고가", example = "2870.5")
    BigDecimal highPrice,
    @Schema(description = "저가", example = "2795.3")
    BigDecimal lowPrice,
    @Schema(description = "전일 대비 등락", example = "50.5")
    BigDecimal versus,
    @Schema(description = "전일 대비 등락률", example = "1.8")
    BigDecimal fluctuationRate,
    @Schema(description = "거래량", example = "1250000")
    Long tradingQuantity,
    @Schema(description = "거래대금", example = "3500000000")
    Long tradingPrice,
    @Schema(description = "상장 시가 총액", example = "450000000000")
    Long marketTotalAmount) {

}
