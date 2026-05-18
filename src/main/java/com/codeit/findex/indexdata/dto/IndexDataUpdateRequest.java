package com.codeit.findex.indexdata.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "지수 데이터 수정 요청")
public record IndexDataUpdateRequest(

    @Schema(description = "시가", example = "2800.25")
    @NotNull(message = "시가는 필수 입력 값입니다.")
    BigDecimal marketPrice,
    @Schema(description = "종가", example = "2850.75")
    @NotNull(message = "종가는 필수 입력 값입니다.")
    BigDecimal closingPrice,
    @Schema(description = "고가", example = "2870.5")
    @NotNull(message = "고가는 필수 입력 값입니다.")
    BigDecimal highPrice,
    @Schema(description = "저가", example = "2795.3")
    @NotNull(message = "저가는 필수 입력 값입니다.")
    BigDecimal lowPrice,
    @Schema(description = "전일 대비 등락", example = "50.5")
    @NotNull(message = "전일 대비 등락은 필수 입력 값입니다.")
    BigDecimal versus,
    @Schema(description = "전일 대비 등락률", example = "1.8")
    @NotNull(message = "전일 대비 등락률은 필수 입력 값입니다.")
    BigDecimal fluctuationRate,
    @Schema(description = "거래량", example = "1250000")
    @NotNull(message = "거래량은 필수 입력 값입니다.")
    Long tradingQuantity,
    @Schema(description = "거래대금", example = "3500000000")
    @NotNull(message = "거래대금은 필수 입력 값입니다.")
    Long tradingPrice,
    @Schema(description = "상장 시가 총액", example = "450000000000")
    @NotNull(message = "상장 시가 총액은 필수 입력 값입니다.")
    Long marketTotalAmount
) {}